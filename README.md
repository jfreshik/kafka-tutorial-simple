# Kafka 연동 테스트 with SpringBoot


## Kafka 

* `wurstmeister/kafka` 도커 이미지로 Kafka 구동
    * docker: https://hub.docker.com/r/wurstmeister/kafka
    * github: https://github.com/wurstmeister/kafka-docker
 
```yaml
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"
  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

```

```shell script
$ docker-compose up -d
```

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
