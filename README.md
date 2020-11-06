# Kafka 연동 테스트 with SpringBoot


## Kafka 정보
bootstrap servers:

|    hostname   |    address   | port |
|:-------------:|:------------:|:----:|
| aechelabapd01 | 10.6.120.56  | 9092 |
| aechelabapd02 | 10.6.120.121 | 9092 |
| aechelabapd03 | 10.6.120.67  | 9092 |

topic: **chelab-topic**

## Producer

application.yml
```yaml
server:
  port: 8088

kafka:
  bootstrap-servers: 10.6.120.56:9092,10.6.120.121:9092,10.6.120.67:9092

  topic:
    name: chelab-topic
    partitions: 1
    replication-factors: 3

```

### 메시지 발송

```shell script
$ curl -X POST http://localhost:8088/produce?message=hello
```

## Consumer

application.yml
```yaml
server:
  port: 8089

kafka:
  bootstrap-servers: 10.6.120.56:9092,10.6.120.121:9092,10.6.120.67:9092

  topic:
    name: chelab-topic
    partitions: 1
    replication-factors: 3

  consumer:
    group-id: chelab-consumer-group01

```



KafkaConsumerConfig.java
```java
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

```
>*@EnableKafka* 어노테이션을 사용해야 *@KafkaListener* 사용할 수 있음

# Reference
[Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)
[Spring for Apache Kafka](https://docs.spring.io/spring-kafka/reference/html/)
