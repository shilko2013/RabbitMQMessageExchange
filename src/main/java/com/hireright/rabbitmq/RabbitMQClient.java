package com.hireright.rabbitmq;

import com.hireright.util.PropertiesReader;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMQClient implements AutoCloseable {

    private final static String defaultPathToResources
            = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "rabbitmq.properties";
    private static String DEFAULT_EXCHANGE_NAME = "default_exchange_name";
    private static String DEFAULT_QUEUE_NAME = "default_queue_name";

    private final ConnectionFactory connectionFactory;
    private Channel channel;
    private int closeTimeSeconds;

    static {
        PropertiesReader propertiesReader = new PropertiesReader(defaultPathToResources);
        setDefaultFields(propertiesReader);
    }

    private static void setDefaultFields(PropertiesReader propertiesReader) {
        setDefaultExchangeName(propertiesReader);
        setDefaultQueueName(propertiesReader);
    }

    private static void setDefaultQueueName(PropertiesReader propertiesReader) {
        String queueName = propertiesReader.getProperty("rabbitmq.queue.name");
        if (queueName == null) //default init value
            return;
        DEFAULT_QUEUE_NAME = queueName;
    }

    private static void setDefaultExchangeName(PropertiesReader propertiesReader) {
        String exchangeName = propertiesReader.getProperty("rabbitmq.exchange.name");
        if (exchangeName == null) //default init value
            return;
        DEFAULT_EXCHANGE_NAME = exchangeName;
    }

    public RabbitMQClient() {
        this(new ConnectionFactory());
    }

    public RabbitMQClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Channel getChannel() throws IOException, TimeoutException {
        if (channel != null)
            return channel;
        return initChannel();
    }

    private Channel initChannel() throws IOException, TimeoutException {
        channel = connectionFactory.newConnection().createChannel();
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
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, true, false, false, null);
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

    public int getCloseTimeSeconds() {
        return closeTimeSeconds;
    }

    @Override
    public void close() throws Exception {
        Connection connection = channel.getConnection();
        channel.close();
        connection.close();
    }
}