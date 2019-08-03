package com.hireright.rabbitmq.producer;

public interface MessageProducer extends AutoCloseable {

    void sendMessage(String message) throws Exception;
    void sendMessage(String destination, String message) throws Exception;

}
