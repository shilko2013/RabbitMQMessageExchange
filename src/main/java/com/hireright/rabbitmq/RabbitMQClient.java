package com.hireright.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMQClient implements AutoCloseable {

    private final static String defaultPathToResources
            = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "rabbitmq.properties";
    private static String DEFAULT_EXCHANGE_NAME = "default_exchange_name";
    private static String DEFAULT_QUEUE_NAME = "default_queue_name";

    private final ConnectionFactory connectionFactory;
    private Channel channel;

    private final static Logger log = LoggerFactory.getLogger(RabbitMQClient.class);

    static {
        try (FileReader reader = new FileReader(defaultPathToResources)) {
            Properties properties = new Properties();
            properties.load(reader);
            log.debug("Reading config from " + defaultPathToResources);
            setDefaultFields(properties);
        } catch (Exception e) {
            log.debug("Config file " + defaultPathToResources + " doesn't exist. Using default values...");
        }
    }

    private static void setDefaultFields(Properties properties) {
        setDefaultExchangeName(properties);
        setDefaultQueueName(properties);
    }

    private static void setDefaultQueueName(Properties properties) {
        DEFAULT_QUEUE_NAME = properties.getProperty("rabbitmq.queue.name");
        log.debug("Default queue name from file: " + DEFAULT_QUEUE_NAME);
    }

    private static void setDefaultExchangeName(Properties properties) {
        DEFAULT_EXCHANGE_NAME = properties.getProperty("rabbitmq.exchange.name");
        log.debug("Default exchange name from file: " + DEFAULT_EXCHANGE_NAME);
    }

    public RabbitMQClient() {
        this(new ConnectionFactory());
    }

    public RabbitMQClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        log.debug("Init client with connection factory");
    }

    public Channel getChannel() throws IOException, TimeoutException {
        if (channel != null)
            return channel;
        return initChannel();
    }

    private Channel initChannel() throws IOException, TimeoutException {
        log.debug("Opening channel...");
        channel = connectionFactory.newConnection().createChannel();
        log.debug("Creating exchange...");
        channel.exchangeDeclare(DEFAULT_EXCHANGE_NAME, "direct", true);
        createQueue();
        return channel;
    }

    public AMQP.Queue.DeclareOk createQueue() throws IOException {
        return createQueue(DEFAULT_QUEUE_NAME);
    }

    public AMQP.Queue.DeclareOk createQueue(String queueName) throws IOException {
        return createQueue(queueName, queueName);
    }

    public AMQP.Queue.DeclareOk createQueue(String queueName, String routingKey) throws IOException {
        log.debug("Creating queue with queue name " + queueName + "...");
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, true, false, false, null);
        log.debug("Binding exchange and queue...");
        channel.queueBind(queueName, DEFAULT_EXCHANGE_NAME, routingKey);
        return declareOk;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public static String getDefaultPathToResources() {
        return defaultPathToResources;
    }

    public static String getDefaultExchangeName() {
        return DEFAULT_EXCHANGE_NAME;
    }

    public static String getDefaultQueueName() {
        return DEFAULT_QUEUE_NAME;
    }

    @Override
    public void close() throws Exception {
        Connection connection = channel.getConnection();
        log.debug("Closing channel...");
        channel.close();
        log.debug("Closing connection...");
        connection.close();
    }
}