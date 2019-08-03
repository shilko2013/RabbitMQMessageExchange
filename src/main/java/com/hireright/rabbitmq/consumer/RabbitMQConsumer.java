package com.hireright.rabbitmq.consumer;

import com.hireright.rabbitmq.RabbitMQClient;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConsumer extends RabbitMQClient implements MessageConsumer {

    public RabbitMQConsumer() {
        super();
    }

    public RabbitMQConsumer(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }


    
}
