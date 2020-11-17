# Json Producer-Consumer

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

## Person Producer 설정

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


## Person Consumer 설정

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



## TEST

```shell script
$ curl -X POST http://localhost:8090/person  -H 'Content-Type: application/json' -d '{"name": "TESTER","age": 40}'
```
> 윈도우 cmd 에서는 curl 로 json 데이터 전송 시 오류 발생. git bash 나 postman 으로 테스트 바람
