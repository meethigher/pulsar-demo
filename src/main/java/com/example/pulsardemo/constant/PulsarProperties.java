package com.example.pulsardemo.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description PulsarProperties
 * @author chenchuancheng
 * @since 2021/9/2 16:56
 */
@Configuration
@ConfigurationProperties(prefix = "pulsar")
public class PulsarProperties {
    private String servers;
    private Producer producer;
    private Consumer consumer;



    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Producer {
        private String topic;
        private String producerName;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getProducerName() {
            return producerName;
        }

        public void setProducerName(String producerName) {
            this.producerName = producerName;
        }
    }
    public static class Consumer {
        private String topic;
        private String subscriptionName;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getSubscriptionName() {
            return subscriptionName;
        }

        public void setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
        }
    }

}
