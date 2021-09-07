package com.example.pulsardemo.config;

import com.example.pulsardemo.constant.PulsarProperties;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author chenchuancheng
 * @description PulsarClientConfig
 * @since 2021/9/2 10:29
 */
@Configuration
public class PulsarClientConfig {
    @Autowired
    private PulsarProperties pulsarProperties;

    /**
     * 获取Client
     *
     * @return
     * @throws PulsarClientException
     * @author chenchuancheng
     * @since 2021/9/6 15:16
     */
    public PulsarClient getClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(pulsarProperties.getServers())
                .build();
    }

    /**
     * 获取生产者
     *
     * @param client
     * @return
     * @throws PulsarClientException
     * @author chenchuancheng
     * @since 2021/9/6 15:16
     */
    public Producer<byte[]> getProducer(PulsarClient client) throws PulsarClientException {
        return client.newProducer()
                .topic(pulsarProperties.getProducer().getTopic())
                .producerName(pulsarProperties.getProducer().getProducerName())
                .sendTimeout(2, TimeUnit.SECONDS)
                .create();
    }


    /**
     * 获取消费者
     *
     * @param client
     * @return
     * @throws PulsarClientException
     * @author chenchuancheng
     * @since 2021/9/6 15:15
     */
    public Consumer getConsumer(PulsarClient client) throws PulsarClientException {
        return client.newConsumer()
                .topic(pulsarProperties.getConsumer().getTopic())
                .subscriptionName(pulsarProperties.getConsumer().getSubscriptionName())
                //配置接收失败，pulsar再次发送消息的时间间隔
                .negativeAckRedeliveryDelay(5, TimeUnit.SECONDS)
                .subscribe();
    }

    /**
     * 关闭
     *
     * @param client
     */
    public void close(PulsarClient client) {
        try {
            client.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

}
