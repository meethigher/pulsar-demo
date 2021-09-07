package com.example.pulsardemo;


import com.example.pulsardemo.config.PulsarClientConfig;
import org.apache.pulsar.client.api.*;

import org.apache.pulsar.shade.org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

@SpringBootTest
class TestAsync {
    @Autowired
    private PulsarClientConfig config;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void testProducer() throws Exception {
        List<CompletableFuture<MessageId>> futures = Lists.newArrayList();
        PulsarClient client = config.getClient();
        Producer<byte[]> producer = config.getProducer(client);
        for (int i = 0; i < 100; i++) {
            final String content = "my-AsyncSend-message-" + i;
            //异步发送
            CompletableFuture<MessageId> future = producer.sendAsync(content.getBytes());
            future.handle(new BiFunction<MessageId, Throwable, Object>() {
                @Override
                public Object apply(MessageId messageId, Throwable throwable) {
                    if (throwable == null) {
                        System.out.println("Message persisted: " + content);
                    } else {
                        System.out.println("Error persisting message: " + content + throwable);
                    }
                    return null;
                }
            });
            futures.add(future);
        }

        System.out.println("Waiting for async ops to complete");
        for (CompletableFuture<MessageId> future : futures) {
            //当完成时返回结果值，如果异常完成则抛出(未检查的)异常。
            future.join();
        }
        System.out.println("All operations completed");

        //关闭
        producer.close();
        client.close();
    }

    @Test
    void testConsumer() throws PulsarClientException {
        PulsarClient client = config.getClient();
        Consumer consumer = config.getConsumer(client);
        List<CompletableFuture<Message>> futures = Lists.newArrayList();
        //消费消息
        for(int i=0;i<100;i++){
            CompletableFuture<Message> future = consumer.receiveAsync();
            System.out.println(i);
            future.handle(new BiFunction<Message, Throwable, Object>() {
                @Override
                public Object apply(Message message, Throwable throwable) {
                    if (throwable == null) {
                        System.out.println("Message consumed: " + new String(message.getData()));
                        try {
                            consumer.acknowledge(message);
                        } catch (PulsarClientException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Error consumed message: " + new String(message.getData()) + throwable);
                        consumer.negativeAcknowledge(message);
                    }
                    return null;
                }
            });
            futures.add(future);

        }
        System.out.println("Waiting for async ops to complete");
        for (CompletableFuture<Message> future : futures) {
            //当完成时返回结果值，如果异常完成则抛出(未检查的)异常。
            future.join();
        }
        System.out.println("All operations completed");
        consumer.close();
        client.close();
    }
}
