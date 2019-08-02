package com.hireright.rabbitmq.producer;

import com.hireright.util.PropertiesReader;
import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer implements AutoCloseable { //TODO: Logger

    private final static String defaultPathToResources
            = "src" + File.pathSeparator + "main" + File.pathSeparator + "resources"
            + File.pathSeparator + "rabbitmq.properties";
    private static int DEFAULT_NUMBER_TASK_THREADS = 10;
    private static int DEFAULT_CLOSE_TIME_SECONDS = 1;
    private static String DEFAULT_EXCHANGE_NAME = "default_exchange_name";
    private static String DEFAULT_QUEUE_NAME = "default_queue_name";

    private final ConnectionFactory connectionFactory;
    private Channel channel;
    private final ExecutorService executor;
    private int closeTimeSeconds;

    static {
        PropertiesReader propertiesReader = new PropertiesReader(defaultPathToResources);
        setDefaultFields(propertiesReader);
    }

    private static void setDefaultFields(PropertiesReader propertiesReader) {
        setDefaultNumberTaskThreads(propertiesReader);
        setDefaultCloseTimeSeconds(propertiesReader);
        setDefaultExchangeName(propertiesReader);
        setDefaultQueueName(propertiesReader);
    }

    private static void setDefaultQueueName(PropertiesReader propertiesReader) {
        String queueName = propertiesReader.getProperty("producer.queue.name");
        if (queueName == null) //default init value
            return;
        DEFAULT_QUEUE_NAME = queueName;
    }

    private static void setDefaultExchangeName(PropertiesReader propertiesReader) {
        String exchangeName = propertiesReader.getProperty("producer.exchange.name");
        if (exchangeName == null) //default init value
            return;
        DEFAULT_EXCHANGE_NAME = exchangeName;
    }

    private static void setDefaultNumberTaskThreads(PropertiesReader propertiesReader) {
        String numberThreads = propertiesReader.getProperty("producer.numthreads");
        if (numberThreads == null) //default init value
            return;
        if ((DEFAULT_NUMBER_TASK_THREADS = Integer.parseInt(numberThreads)) < 1)
            throw new IllegalArgumentException();
    }

    private static void setDefaultCloseTimeSeconds(PropertiesReader propertiesReader) {
        String closeTimeSeconds = propertiesReader.getProperty("producer.closetime");
        if (closeTimeSeconds == null) //default init value
            return;
        if ((DEFAULT_CLOSE_TIME_SECONDS = Integer.parseInt(closeTimeSeconds)) < 0)
            throw new IllegalArgumentException();
    }

    /*
            Runnable worker = new MyRunnable(10000000L + i);
            executor.execute(worker);
    */

    public RabbitMQProducer() {
        this(new ConnectionFactory());
    }

    public RabbitMQProducer(ConnectionFactory connectionFactory) {
        this(connectionFactory, DEFAULT_NUMBER_TASK_THREADS);
    }

    public RabbitMQProducer(ConnectionFactory connectionFactory, int numberTaskThreads) {
        this.connectionFactory = connectionFactory;
        executor = Executors.newFixedThreadPool(numberTaskThreads);
        closeTimeSeconds = DEFAULT_CLOSE_TIME_SECONDS;
    }

    public void setCloseTimeSeconds(int closeTimeSeconds) {
        if (closeTimeSeconds < 0)
            throw new IllegalArgumentException();
        this.closeTimeSeconds = closeTimeSeconds;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    private Channel getChannel() throws IOException, TimeoutException {
        if (channel != null)
            return channel;
        return initChannel();
    }

    private Channel initChannel() throws IOException, TimeoutException {
        channel = connectionFactory.newConnection().createChannel();
        channel.exchangeDeclare(DEFAULT_EXCHANGE_NAME, "direct", true);
        return channel;
    }

    public Queue.DeclareOk createQueue() throws IOException {
        return createQueue(DEFAULT_QUEUE_NAME);
    }

    public Queue.DeclareOk createQueue(String queueName) throws IOException {
        return createQueue(queueName, queueName);
    }

    public Queue.DeclareOk createQueue(String queueName, String routingKey) throws IOException {
        Queue.DeclareOk declareOk = channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, DEFAULT_EXCHANGE_NAME, routingKey);
        return declareOk;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(DEFAULT_QUEUE_NAME, message);
    }

    public void sendMessage(String routingKey, String message) throws IOException {
        channel.basicPublish(DEFAULT_EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }

    @Override
    public void close() throws IOException, TimeoutException, InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(closeTimeSeconds, TimeUnit.SECONDS))
            throw new TimeoutException();
        Connection connection = channel.getConnection();
        channel.close();
        connection.close();
    }


}
