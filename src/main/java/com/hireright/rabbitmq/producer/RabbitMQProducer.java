package com.hireright.rabbitmq.producer;

import com.hireright.util.PropertiesReader;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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

    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;
    private final ExecutorService executor;
    private int closeTimeSeconds;

    static {
        PropertiesReader propertiesReader = new PropertiesReader(defaultPathToResources);
        setDefaultNumberTaskThreads(propertiesReader);
        setDefaultCloseTimeSeconds(propertiesReader);
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

    public void openChannel() throws IOException, TimeoutException {
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
    }

    @Override
    public void close() throws IOException, TimeoutException, InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(closeTimeSeconds, TimeUnit.SECONDS))
            throw new TimeoutException();
        channel.close();
        connection.close();
    }


}
