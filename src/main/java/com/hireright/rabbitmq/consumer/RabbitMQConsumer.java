package com.hireright.rabbitmq.consumer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer extends RabbitMQClient implements MessageConsumer {

    private boolean autoAck = false;

    public RabbitMQConsumer() {
        super();
    }

    public RabbitMQConsumer(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public RabbitMQConsumer(ConnectionFactory connectionFactory, boolean autoAck) {
        super(connectionFactory);
        this.autoAck = autoAck;
    }

    @Override
    public void addConsumer(String consumerTag, Consumer consumer) throws IOException, TimeoutException {
        addConsumer(getDefaultExchangeName(), autoAck, consumerTag, consumer);
    }

    @Override
    public void addConsumer(boolean autoAck, String consumerTag, Consumer consumer) throws IOException, TimeoutException {
        addConsumer(getDefaultQueueName(), autoAck, consumerTag, consumer);
    }

    @Override
    public void addConsumer(String queueName, String consumerTag, Consumer consumer) throws IOException, TimeoutException {
        addConsumer(queueName, autoAck, consumerTag, consumer);
    }

    @Override
    public void addConsumer(String queueName, boolean autoAck, String consumerTag, Consumer consumer) throws IOException, TimeoutException {
        getChannel().basicConsume(queueName, autoAck, consumerTag, consumer);
    }
}
