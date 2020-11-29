# Kafka Consumer

## application.yml
```yaml
server:
  port: 8089

spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092

    topic: simple-topic

    consumer:
      group-id: simple-consumer-group
```



KafkaConsumerConfig.java
```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

```
>*@EnableKafka* 어노테이션을 사용해야 *@KafkaListener* 사용할 수 있음


ConsumerHandler.java
```java
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        System.out.println("Received Message in listen01: " + message);
    }
```
> @KafkaListener 에 토픽과 groupId 를 지정

