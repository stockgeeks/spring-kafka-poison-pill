# Spring Kafka beyond the basics - Poison Pill

Example project used for Conference talk and Confluent blog post.

Using:

* Confluent Kafka 5.5
* Confluent Schema Registry 5.5
* Java 8
* Spring Boot 2.3
* Spring Kafka 2.5
* Apache Avro 1.9.2

## Project modules and applications

| Applications                                  | Port | Avro  | Topic(s)          | Description                                                              |
|-----------------------------------------------|------|-------|-------------------|--------------------------------------------------------------------------|
| stock-quote-producer-avro                     | 8080 | YES  | stock-quotes-avro  | Simple producer of random stock quotes using Spring Kafka & Apache Avro. |
| stock-quote-consumer-avro                     | 8082 | YES  | stock-quotes-avro  | Simple consumer of stock quotes using using Spring Kafka & Apache Avro.  |

| Module                                   | Description                                                             |
|------------------------------------------|-------------------------------------------------------------------------|
| stock-quote-avro-model                   | Holds the Avro schema for the Stock Quote including `avro-maven-plugin` to generate Java code based on the Avro Schema. This module is used by both the producer and consumer application.  |

Note Confluent Schema Registry is running on port: `8081` using Docker (see [docker-compose.yml](docker-compose.yml). 

## Goal

The goal of this example project is to show how protect your Kafka application against Deserialization exceptions (a.k.a. poison pills) leveraging Spring Boot and Spring Kafka.

This example project has 3 different branches:

* `master` : no configuration to protect the consumer application (`stock-quote-consumer-avro`) against the poison pill scenario.
* `handle-poison-pill-log-and-continue-consuming` : configuration to protect the consumer application (`stock-quote-consumer-avro`) against the poison pill scenario by simply logging the poison pill(s) and continue consuming.
* TODO : configuration to protect the consumer application (`stock-quote-consumer-avro`) against the poison pill scenario by publishing the poison pill(s) to a dead letter topic `stock-quotes-avro.DLT` and continue consuming.

## Compile to the project

```
./mvnw clean package
```

## Start / Stop Kafka & Zookeeper

```
docker-compose up -d
```

```
docker-compose down -v
```

## Execute the Poison Pill scenario yourself

First make sure all Docker containers started successfully (state: `Up`):

```
docker-compose ps
``` 

### Start both producer and consumer

* Spring Boot application: `stock-quote-producer-avro`

```
java -jar stock-quote-producer-avro/target/stock-quote-producer-avro-0.0.1-SNAPSHOT.jar
```

* Spring Boot application: `stock-quote-consumer-avro`

```
java -jar stock-quote-consumer-avro/target/stock-quote-consumer-avro-0.0.1-SNAPSHOT.jar
```

### Attach to Kafka

```
docker exec -it kafka bash
```

### Unset JMX Port 

To avoid error:

```
unset JMX_PORT
```

```
Error: JMX connector server communication error: service:jmx:rmi://kafka:9999
sun.management.AgentConfigurationError: java.rmi.server.ExportException: Port already in use: 9999; nested exception is: 
        java.net.BindException: Address already in use (Bind failed)
        at sun.management.jmxremote.ConnectorBootstrap.exportMBeanServer(ConnectorBootstrap.java:800)
        at sun.management.jmxremote.ConnectorBootstrap.startRemoteConnectorServer(ConnectorBootstrap.java:468)
        at sun.management.Agent.startAgent(Agent.java:262)
        at sun.management.Agent.startAgent(Agent.java:452)
Caused by: java.rmi.server.ExportException: Port already in use: 9999; nested exception is: 
        java.net.BindException: Address already in use (Bind failed)
        at sun.rmi.transport.tcp.TCPTransport.listen(TCPTransport.java:346)
        at sun.rmi.transport.tcp.TCPTransport.exportObject(TCPTransport.java:254)
        at sun.rmi.transport.tcp.TCPEndpoint.exportObject(TCPEndpoint.java:411)
        at sun.rmi.transport.LiveRef.exportObject(LiveRef.java:147)
        at sun.rmi.server.UnicastServerRef.exportObject(UnicastServerRef.java:237)
        at sun.management.jmxremote.ConnectorBootstrap$PermanentExporter.exportObject(ConnectorBootstrap.java:199)
        at javax.management.remote.rmi.RMIJRMPServerImpl.export(RMIJRMPServerImpl.java:146)
        at javax.management.remote.rmi.RMIJRMPServerImpl.export(RMIJRMPServerImpl.java:122)
        at javax.management.remote.rmi.RMIConnectorServer.start(RMIConnectorServer.java:404)
        at sun.management.jmxremote.ConnectorBootstrap.exportMBeanServer(ConnectorBootstrap.java:796)
        ... 3 more
Caused by: java.net.BindException: Address already in use (Bind failed)
        at java.net.PlainSocketImpl.socketBind(Native Method)
        at java.net.AbstractPlainSocketImpl.bind(AbstractPlainSocketImpl.java:387)
        at java.net.ServerSocket.bind(ServerSocket.java:375)
        at java.net.ServerSocket.<init>(ServerSocket.java:237)
        at java.net.ServerSocket.<init>(ServerSocket.java:128)
        at sun.rmi.transport.proxy.RMIDirectSocketFactory.createServerSocket(RMIDirectSocketFactory.java:45)
        at sun.rmi.transport.proxy.RMIMasterSocketFactory.createServerSocket(RMIMasterSocketFactory.java:345)
        at sun.rmi.transport.tcp.TCPEndpoint.newServerSocket(TCPEndpoint.java:666)
        at sun.rmi.transport.tcp.TCPTransport.listen(TCPTransport.java:335)
        ... 12 more
```

### Start the Kafka console producer from the command line

```
./usr/bin/kafka-console-producer --broker-list localhost:9092 --topic stock-quotes-avro
```

### Publish the poison pill ;)

The console is waiting for input. Now publish the poison pill:

```
💊
```

### Check the consumer application!

The consumer application will try to deserialize the poison pill but fail.
The application by default will try again, again and again to deserialize the record but will never succeed!. 
For every deserialization exception a log line will be written to the log:

```
java.lang.IllegalStateException: This error handler cannot process 'SerializationException's directly; please consider configuring an 'ErrorHandlingDeserializer' in the value and/or key deserializer
        at org.springframework.kafka.listener.SeekUtils.seekOrRecover(SeekUtils.java:145) ~[spring-kafka-2.5.0.RELEASE.jar!/:2.5.0.RELEASE]
        at org.springframework.kafka.listener.SeekToCurrentErrorHandler.handle(SeekToCurrentErrorHandler.java:103) ~[spring-kafka-2.5.0.RELEASE.jar!/:2.5.0.RELEASE]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.handleConsumerException(KafkaMessageListenerContainer.java:1241) ~[spring-kafka-2.5.0.RELEASE.jar!/:2.5.0.RELEASE]
        at org.springframework.kafka.listener.KafkaMessageListenerContainer$ListenerConsumer.run(KafkaMessageListenerContainer.java:1002) ~[spring-kafka-2.5.0.RELEASE.jar!/:2.5.0.RELEASE]
        at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515) ~[na:na]
        at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264) ~[na:na]
        at java.base/java.lang.Thread.run(Thread.java:834) ~[na:na]
Caused by: org.apache.kafka.common.errors.SerializationException: Error deserializing key/value for partition stock-quotes-avro-1 at offset 69. If needed, please seek past the record to continue consumption.
Caused by: org.apache.kafka.common.errors.SerializationException: Unknown magic byte!
```

Make sure to stop the consumer application!

## How to survive a poison pill scenario?

Now it's time protect the consumer application!
Spring Kafka offers excellent support to protect your Kafka application(s) against the poison pill scenario.
Depending on your use case you have different options.  

### Log the poison pill(s) and continue consuming

By configuring Spring Kafka's: `org.springframework.kafka.support.serializer.ErrorHandlingDeserializer` 

Branch:

```
git checkout handle-poison-pill-log-and-continue-consuming
```

Configuration of the Consumer Application (`application.yml`) to configure the: ErrorHandlingDeserializer 

```yml
spring:
  kafka:
    consumer:
      # Configures the Spring Kafka ErrorHandlingDeserializer that delegates to the 'real' deserializers
      key-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
    properties:
      # Delegate deserializers
      spring.deserializer.key.delegate.class: org.apache.kafka.common.serialization.StringDeserializer
      spring.deserializer.value.delegate.class: io.confluent.kafka.serializers.KafkaAvroDeserializer
```

### Publish poison pill(s) a dead letter topic and continue consuming

```

```