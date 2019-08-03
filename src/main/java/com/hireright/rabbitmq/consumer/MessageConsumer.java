package com.hireright.rabbitmq.consumer;

import com.rabbitmq.client.Consumer;

public interface MessageConsumer {

    void addConsumer(String consumerTag, Consumer consumer) throws Exception;

    void addConsumer(boolean autoAck, String consumerTag, Consumer consumer) throws Exception;

    void addConsumer(String queueName, String consumerTag, Consumer consumer) throws Exception;

    void addConsumer(String queueName, boolean autoAck, String consumerTag, Consumer consumer) throws Exception;

}
