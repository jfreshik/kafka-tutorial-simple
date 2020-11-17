package com.chealb.kafka.consumer;

import com.chealb.kafka.model.Person;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PersonConsumerHandler {

    // json 을 person 으로 deserialize 하는
    // containerFactory 를 지정해 주어야 함
    @KafkaListener(topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "personConcurrentKafkaListenerContainerFactory")
    public void onPersonMessage(Person person) {
        System.out.println("Received person: [" + person + "]");
    }

}
