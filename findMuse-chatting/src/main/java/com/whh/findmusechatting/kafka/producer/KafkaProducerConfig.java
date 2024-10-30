package com.whh.findmusechatting.kafka.producer;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, ChatMessage> messageKafkaTemplate() {
        return new ReactiveKafkaProducerTemplate<>(
                SenderOptions.<String, ChatMessage>create(producerConfig())
                        .maxInFlight(1024)
        );
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, ChatNotification> notificationKafkaTemplate() {
        return new ReactiveKafkaProducerTemplate<>(
                SenderOptions.<String, ChatNotification>create(producerConfig())
                        .maxInFlight(1024)
        );
    }
}