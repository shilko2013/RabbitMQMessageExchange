package com.hireright.rabbitmq.demonstration;

import com.hireright.rabbitmq.consumer.RabbitMQConsumer;
import com.hireright.util.SimpleConsumer;

public class RabbitMQConsumerDemonstration {

    public static void main(String[] args) {
        System.out.println("See the messages below: ");
        try (RabbitMQConsumer consumer = new RabbitMQConsumer()) {
            consumer.addConsumer("consumerTag", new SimpleConsumer(consumer.getChannel(), System.out::println));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
