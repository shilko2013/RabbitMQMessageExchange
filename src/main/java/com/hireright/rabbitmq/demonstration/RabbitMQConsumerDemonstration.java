package com.hireright.rabbitmq.demonstration;

import com.hireright.rabbitmq.consumer.RabbitMQConsumer;
import com.hireright.util.SimpleConsumer;

import java.util.Scanner;

public class RabbitMQConsumerDemonstration {

    public static void main(String[] args) {
        System.out.println("Type ENTER to exit. See the messages below: ");
        try (RabbitMQConsumer consumer = new RabbitMQConsumer()) {
            consumer.addConsumer("consumerTag", new SimpleConsumer(consumer.getChannel(), System.out::println));
            while (System.in.read() == -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
