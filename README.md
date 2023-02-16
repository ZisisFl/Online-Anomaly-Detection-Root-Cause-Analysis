# Overview
This project is the implementation of my Thesis with title `Online Anomaly Detection and Root Cause Analysis` for the Master program [Data and Web Science](https://dws.csd.auth.gr/) in the [Department of Informatics](https://www.csd.auth.gr/en/) of [Aristotle University of Thessaloniki](https://www.auth.gr/en/).

# Project structure
This project consists of 4 main components.
- **dsdgen:** Tooling to generate data using [TPC-DS tool](https://www.tpc.org/tpcds/)
- **kafka_producer:** Kafka Python producer that produces records of SQL tables as JSON records into topics
- **online-ad-rca:** Scala Flink project to perform the Online Anomaly Detection and Root Cause Analysis over Kafka topics
- **docker-compose.yaml:** Docker based version of the infrastructure required to reproduce this work.

## online-ad-rca
This Flink project is using Flink version Scala 2.12 and Java 11.

## docker-compose
The infrastructure provided in the form of a docker-compose configuration includes a single Apache Kafka (along with Zookeeper) broker that is used for streaming of messages and a Apache Flink cluster. The Flink cluster consists of a JobManager and four TaskManagers.

When developing it isn't required to start up the Flink Cluster this is why the default [profile](https://docs.docker.com/compose/profiles/) in the docker-compose will start up only the Kafka broker and the Zookeeper
```bash
docker-compose up -d
```
To start also the Flink cluster you need to use the `flink-cluster` profile like below:
 ```bash
 docker-compose --profile flink-cluster up -d
 ```





# Flink
## To generate jar of jobs
Run on project folder `sbt assembly`  
The final jar is on folder `/target/scala-2.12/online-rca-0.1.jar`

To start docker stack with Flink
docker-compose --profile flink-cluster up -d

Docker compose
Flink UI http://localhost:8081/#/overview


Notes flink
https://stackoverflow.com/questions/64513940/apache-flink-how-to-implement-custom-deserializer-implementing-deserialization
https://stackoverflow.com/questions/51648705/how-to-deserialize-when-json-data-in-kafka-is-not-clear-when-using-flink