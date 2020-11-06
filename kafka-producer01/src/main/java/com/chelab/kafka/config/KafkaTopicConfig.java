package com.chelab.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${kafka.topic.name}")
    private String topicName;

    @Value(value = "${kafka.topic.partitions}")
    private int partitions;

    @Value(value = "${kafka.topic.replication-factors}")
    private int replicationFactors;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic chelabTopic() {
        // topic-name, partition, replication-factor
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicationFactors)
                .compact()
                .build();
    }
}
