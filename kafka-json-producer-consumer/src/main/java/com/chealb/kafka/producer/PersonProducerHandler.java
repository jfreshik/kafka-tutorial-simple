package com.chealb.kafka.producer;

import com.chealb.kafka.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PersonProducerHandler {

    @Value("${spring.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<String, Person> kafkaTemplate;

    @PostMapping("/person")
    public String publishPerson(@RequestBody Person person) {
        ListenableFuture<SendResult<String, Person>> future = kafkaTemplate.send(topicName, person);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Person>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message=[" + person + "] due to : " + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Person> result) {
                System.out.println("Sent message=[" + person + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
        });

        return "OK";
    }
}
