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



## Json 송수신
kafka-json-producer-consumer 프로젝트

### Person Producer 설정

* Person 클래스
  * name, age 필드
  * ToString override
```java
@Getter
@Setter
@ToString
public class Person {
    private String name;
    private int age;
}
```

* ProducerFactory 설정:
  * value serializer 를 `JsonSerializer` 로 지정

* KafkaTemplate 설정:
  * 생성 시 personProducerFactory 를 주입

```java
    @Bean
    public ProducerFactory<String, Person> personProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // JsonSerializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Person> personKafkaTemplate() {
        // kafkaTemplate 생성 시 personProducerFactory 지정
        return new KafkaTemplate<>(personProducerFactory());
    }
```


### Person Consumer 설정

* ConsumerFactory 설정:
  * value serializer 를 `JsonDeserializer` 로 지정
  * factory 생성 시 주입하는 deserialize 에 Person class 전달

* KafkaListenerContainerFactory 설정:
  * KafkaListener 에서 사용할 container factory
  * Person 을 deserialize 하는 personConsumerFactory 사용 

```java
    @Bean
    public ConsumerFactory<String, Person> personConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
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
```

* Person consumer handler
```java
    // json 을 person 으로 deserialize 하는
    // containerFactory 를 지정해 주어야 함
    @KafkaListener(topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "personConcurrentKafkaListenerContainerFactory")
    public void onPersonMessage(Person person) {
        System.out.println("Received person: [" + person + "]");
    }
```

* TEST
```shell script
$ curl -X POST http://localhost:8090/person  -H 'Content-Type: application/json' -d '{"name": "TESTER","age": 40}'
```
> 윈도우 cmd 에서는 curl 로 json 데이터 전송 시 오류 발생. git bash 나 postman 으로 테스트 바람


# Reference
[Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)
[Spring for Apache Kafka](https://docs.spring.io/spring-kafka/reference/html/)
