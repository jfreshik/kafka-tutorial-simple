# Kafka Consumer

## application.yml
```yaml
server:
  port: 8089

spring:
  kafka:
    bootstrap-servers: 10.6.120.56:9092,10.6.120.121:9092,10.6.120.67:9092

    topic: chelab-topic

    consumer:
      group-id: chelab-consumer-group
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

