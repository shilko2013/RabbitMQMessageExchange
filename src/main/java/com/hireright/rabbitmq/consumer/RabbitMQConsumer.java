package com.hireright.rabbitmq.consumer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer extends RabbitMQClient implements MessageConsumer {

    private Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

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
        addConsumer(getDefaultQueueName(), autoAck, consumerTag, consumer);
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
        log.debug("Consumer added in queue " + queueName + ", consumer tag = " + consumerTag);
        getChannel().basicConsume(queueName, autoAck, consumerTag, consumer);
    }
}
