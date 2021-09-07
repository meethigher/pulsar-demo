package com.example.pulsardemo;

import com.example.pulsardemo.config.PulsarClientConfig;
import org.apache.pulsar.client.api.*;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.format.DateTimeFormatter;

@SpringBootTest
class TestSync {
    @Autowired
    private PulsarClientConfig config;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void testProducer() throws Exception {
        PulsarClient client = config.getClient();
        Producer<byte[]> producer = config.getProducer(client);
        for (int i = 0; i < 100; i++) {
            final String content = "my-SyncSend-message-" + i;
            producer.send((content).getBytes());//同步发送
            System.out.println("Send message: " + content);
        }
        //关闭
        producer.close();
        client.close();
    }

    @Test
    void testConsumer() throws PulsarClientException {
        PulsarClient client = config.getClient();
        Consumer consumer = config.getConsumer(client);
        //消费消息
        while (true) {
            Message message = consumer.receive();
            try {
                System.out.printf("Message received: %s%n", new String(message.getData()));
                consumer.acknowledge(message);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(message);
            }
        }
    }
}
