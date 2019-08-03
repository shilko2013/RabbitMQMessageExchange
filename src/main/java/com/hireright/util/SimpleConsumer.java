package com.hireright.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.function.Consumer;

public class SimpleConsumer extends DefaultConsumer {

    private final Consumer<String> consumer;

    public SimpleConsumer(Channel channel, Consumer<String> consumer) {
        super(channel);
        this.consumer = consumer;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        long deliveryTag = envelope.getDeliveryTag();
        consumer.accept(new String(body));
        getChannel().basicAck(deliveryTag, false);
    }
}
