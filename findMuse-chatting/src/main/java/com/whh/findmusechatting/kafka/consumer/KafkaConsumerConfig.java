package com.whh.findmusechatting.kafka.consumer;

import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.topic.message}")
    private String messageTopic;

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    @Bean
    public Map<String, Object> consumerConfig() {
        log.info("bootstrapServers: {}", bootstrapServers);
        log.info("groupId: {}", groupId);
        log.info("autoOffsetReset: {}", autoOffsetReset);
        log.info("messageTopic: {}", messageTopic);
        log.info("notificationTopic: {}", notificationTopic);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // ErrorHandlingDeserializer 설정
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean
    public KafkaReceiver<String, CreateChatMessageRequest> messageKafkaReceiver() {
        ReceiverOptions<String, CreateChatMessageRequest> receiverOptions = ReceiverOptions
                .<String, CreateChatMessageRequest>create(consumerConfig())
                .subscription(Collections.singleton(messageTopic))
                .withKeyDeserializer(new ErrorHandlingDeserializer<>(new StringDeserializer()))
                .withValueDeserializer(new ErrorHandlingDeserializer<>(
                        new JsonDeserializer<>(CreateChatMessageRequest.class, false)
                ));

        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaReceiver<String, ChatNotification> notificationKafkaReceiver() {
        ReceiverOptions<String, ChatNotification> receiverOptions = ReceiverOptions
                .<String, ChatNotification>create(consumerConfig())
                .subscription(Collections.singleton(notificationTopic))
                .withKeyDeserializer(new ErrorHandlingDeserializer<>(new StringDeserializer()))
                .withValueDeserializer(new ErrorHandlingDeserializer<>(
                        new JsonDeserializer<>(ChatNotification.class, false)
                ));

        return KafkaReceiver.create(receiverOptions);
    }
}