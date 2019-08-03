package com.hireright.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class SimpleConsumer extends DefaultConsumer {

    private static Logger log = LoggerFactory.getLogger(SimpleConsumer.class);

    private final Consumer<String> consumer;

    public SimpleConsumer(Channel channel, Consumer<String> consumer) {
        super(channel);
        this.consumer = consumer;
        log.debug("Consumer created.");
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        log.debug("Message has been received");
        long deliveryTag = envelope.getDeliveryTag();
        log.debug("Consumer running...");
        consumer.accept(new String(body));
        log.debug("Acknowledging queue...");
        getChannel().basicAck(deliveryTag, false);
    }
}
