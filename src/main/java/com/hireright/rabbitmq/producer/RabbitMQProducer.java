package com.hireright.rabbitmq.producer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer extends RabbitMQClient implements MessageProducer {

    private Logger log = LoggerFactory.getLogger(RabbitMQProducer.class);

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
        String shortMessage = message.length() > 50 ? message.substring(50) + "..." : message;
        log.debug("Sending message with routing key = " + routingKey + " with body: " + shortMessage);
        getChannel().basicPublish(getDefaultExchangeName(), routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }

}
