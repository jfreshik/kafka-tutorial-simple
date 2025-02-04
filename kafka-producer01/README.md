# Kafka Producer

## application.yml
```yaml
server:
  port: 8088

spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092
    topic: simple-topic

    topic-setting:
      partitions: 3
      replicas: 1

```

## 메시지 발송 처리
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


## 메시지 발송 테스트

```shell script
$ curl -X POST http://localhost:8088/publish?message=hello
```
