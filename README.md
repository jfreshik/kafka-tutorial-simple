# Kafka 연동 테스트 with SpringBoot


## Kafka 정보
bootstrap servers:

|    hostname   |    address   | port |
|:-------------:|:------------:|:----:|
| aechelabapd01 | 10.6.120.56  | 9092 |
| aechelabapd02 | 10.6.120.121 | 9092 |
| aechelabapd03 | 10.6.120.67  | 9092 |

topic: **chelab-topic**

### dependency
```xml
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
```

## Producer

### application.yml
```yaml
server:
  port: 8088

spring:
  kafka:
    bootstrap-servers: 10.6.120.56:9092,10.6.120.121:9092,10.6.120.67:9092
    topic: chelab-topic

    topic-setting:
      partitions: 3
      replicas: 3

```

### 메시지 발송 처리
ProducerHandler.java
```java
    @PostMapping("/publish")
    public String produceMessage(@RequestParam String message) {
        sendMessage(message);
        return "OK";
    }

    private void sendMessage(String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Unable to send message=[" + message + "] due to : " + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
        });
    }
```

> kafkaTemplate.send(topicName, message) 으로 메시지 비동기 발송
> ListenableFuture<SendResult<String, String>> future 에 callback 등록해 성공/실패 처리


### 메시지 발송 테스트

```shell script
$ curl -X POST http://localhost:8088/publish?message=hello
```

## Consumer

### application.yml
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

# Reference
[Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)
[Spring for Apache Kafka](https://docs.spring.io/spring-kafka/reference/html/)
