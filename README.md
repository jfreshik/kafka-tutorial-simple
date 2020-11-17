# Kafka 연동 테스트 with SpringBoot


## Kafka 정보
bootstrap servers:

|    hostname   |    address   | port |
|:-------------:|:------------:|:----:|
| aechelabapd01 | 10.6.120.56  | 9092 |
| aechelabapd02 | 10.6.120.121 | 9092 |
| aechelabapd03 | 10.6.120.67  | 9092 |

topic: **chelab-topic**

## dependency
```xml
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
```

## projects
* [Simple Producer](kafka-producer01/README.md)
: 메시지 producer

* [Simple Consumer](kafka-consumer01/README.md)
: 메시지 consumer

* [Json Producer-Consumer](kafka-json-producer-consumer/README.md)
: Person 객체를 Json 형태로 produce,consume


# Reference
[Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)

[Spring for Apache Kafka](https://docs.spring.io/spring-kafka/reference/html/)
