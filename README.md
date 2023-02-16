# Overview
This project is the implementation of my Thesis with title `Online Anomaly Detection and Root Cause Analysis` for the Master program [Data and Web Science](https://dws.csd.auth.gr/) in the [Department of Informatics](https://www.csd.auth.gr/en/) of [Aristotle University of Thessaloniki](https://www.auth.gr/en/).

# Project structure
This project consists of 4 main components.
- **dsdgen:** Tooling to generate data using [TPC-DS tool](https://www.tpc.org/tpcds/)
- **kafka_producer:** Kafka Python producer that produces records of SQL tables as JSON records into topics
- **online-ad-rca:** Scala Flink project to perform the Online Anomaly Detection and Root Cause Analysis over Kafka topics
- **docker-compose.yaml:** Docker based version of the infrastructure required to reproduce this work

## dsdgen
In this directory you can find documentation on how to set up a Docker container instance of TPC-DS tool generate data along with database schema and load them to a PostgreSQL database. There are scripts to automate the various processes required. The directory doesn't contain the TPC-DS tool itself you will need to download it on your on and follow the related instructions.

## kafka_producer
A Kafka producer written in Python is developed in order to populate topics in Kafka broker with data from the SQL TPC-DS database (it can be any [SQLAlchemy](https://www.sqlalchemy.org/) compatible SQL database). You can find instructions on how to set up and run it in the kafka_producer directory.

## online-ad-rca
The core component of this project is the Flink project that implements the Online Anomaly Detection and Root Cause Analysis algorithms. The project is created using Flink version 1.13.2, Scala 2.12 and Java 11.

## docker-compose
The infrastructure is provided in the form of a docker-compose configuration includes a single Apache Kafka (along with Zookeeper) broker that is used for streaming of messages and a Apache Flink cluster. The Flink cluster consists of a JobManager and four TaskManagers.

When developing it isn't required to have the Flink Cluster running, this is why the default [profile](https://docs.docker.com/compose/profiles/) in the docker-compose will start up only the Kafka broker and the Zookeeper
```bash
docker-compose up -d
```
To fire up also the Flink cluster you need to use the `flink-cluster` profile like below:
 ```bash
 docker-compose --profile flink-cluster up -d
 ```

 To access the UI of the Flink cluster go to `http://localhost:8081`

# Flink
## Generate Job jar file
To generate a new jar that can be submitted to the Flink cluster, navigate to the online-ad-rca project folder and the use `sbt assembly` command. This will produce a new jar named `online-ad-rca/target/scala-2.12/online-rca-{VERSION}.jar`