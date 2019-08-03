package com.hireright.rabbitmq.consumer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

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
    public void addConsumer(String consumerTag, Consumer consumer) {

    }

    @Override
    public void addConsumer(boolean autoAck, String consumerTag, Consumer consumer) {

    }

    @Override
    public void addConsumer(String queueName, String consumerTag, Consumer consumer) {

    }

    @Override
    public void addConsumer(String queueName, boolean autoAck, String consumerTag, Consumer consumer) {

    }
}
