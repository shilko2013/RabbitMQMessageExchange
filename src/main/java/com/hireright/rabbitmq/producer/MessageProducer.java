package com.hireright.rabbitmq.producer;

public interface MessageProducer {

    void sendMessage(String message) throws Exception;
    void sendMessage(String destination, String message) throws Exception;

}
