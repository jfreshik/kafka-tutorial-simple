package com.example.kafka.config;

import com.example.kafka.model.Person;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class PersonConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${spring.kafka.consumer.group-id")
    private String groupId;

    // Person 을 값으로 consume 하는 ConsumerFactory 설정
    @Bean
    public ConsumerFactory<String, Person> personConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // kafka - bootstrap servers
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // consumer group id
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // key,value deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Value Deserializer 는 JsonDeserializer 를 이용함
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Deserializer 대상 클래스(Person.class) 기술
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(Person.class));
    }

    // 로 person 을 deserialize 하는 personConsumerFactory 를
    // kafka listener CONTAINER factory 에 등록
    // => KafkaListener 어노테이션의 containerFactory 에 bean 이름 사용
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Person> personConcurrentKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, Person> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(personConsumerFactory());
        return factory;
    }
}
