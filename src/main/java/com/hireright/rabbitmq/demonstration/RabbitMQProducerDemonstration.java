package com.hireright.rabbitmq.demonstration;

import com.hireright.rabbitmq.producer.MessageProducer;
import com.hireright.rabbitmq.producer.RabbitMQProducer;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class RabbitMQProducerDemonstration {

    public static void main(String[] args) {
        System.out.println("Type a text for sending below. Every line is a separate message. Press Crtl+D for quit.");
        try (MessageProducer producer = new RabbitMQProducer(); Scanner in = new Scanner(System.in)) {
            in.nextLine();
            while (in.hasNext()) {
                System.out.print("> ");
                String input = in.nextLine();
                producer.sendMessage(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
