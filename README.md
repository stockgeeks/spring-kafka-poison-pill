# Spring Kafka beyond the basics - Poison Pill

Example project used for the conference talk.

## Project modules and applications

Please note port 8081 is taken by the Confluent Schema registry!

| Application                                   | Port | Avro  | Topic(s)         | Description                                                          |
|-----------------------------------------------|------|-------|------------------|----------------------------------------------------------------------|
| stock-tick-producer-avro                      | 8080 | YES   | stock-ticks-avro | Simple producer of stock ticks using Avro                            |
| stock-tick-consumer-avro                      | 8082 | YES   | stock-ticks-avro | Simple consumer of stock ticks using Avro                            |

Note Confluent Schema Registry is running on port: 8081

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

## Poison Pill scenario

### Start both producer and consumer

* `stock-tick-producer`

```
java -jar stock-tick-producer-avro/target/stock-tick-producer-avro-0.0.1-SNAPSHOT.jar
```

* `stock-tick-consumer`

```
java -jar stock-tick-consumer-avro/target/stock-tick-consumer-avro-0.0.1-SNAPSHOT.jar
```


### Attach to Kafka

```
docker exec -it kafka bash
```

### Unset JMX Port to avoid error

```
unset JMX_PORT
```

### Produce from the command line

#### Avro

```
./usr/bin/kafka-console-producer --broker-list localhost:9092 --topic stock-ticks-avro
```

### Publish the poison pill ;)

💊The console is waiting for input. Now publish the poision pill:

```
💊
```
💊