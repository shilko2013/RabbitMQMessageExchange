package com.hireright.rabbitmq.producer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer extends RabbitMQClient implements MessageProducer {

    public RabbitMQProducer() {
        super();
    }

    public RabbitMQProducer(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void sendMessage(String message) throws IOException, TimeoutException {
        sendMessage(getDefaultQueueName(), message);
    }

    @Override
    public void sendMessage(String routingKey, String message) throws IOException, TimeoutException {
        getChannel().basicPublish(getDefaultExchangeName(), routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }

}
