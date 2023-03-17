# Overview
This project is the implementation of my Thesis with title **Online Anomaly Detection and Root Cause Analysis** for the Master program [Data and Web Science](https://dws.csd.auth.gr/) in the [Department of Informatics](https://www.csd.auth.gr/en/) of [Aristotle University of Thessaloniki](https://www.auth.gr/en/).

This work is mainly inspired from ThirdEye [[1](https://dev.startree.ai/docs/startree-enterprise-edition/startree-thirdeye/)], [[2](https://github.com/startreedata/thirdeye)], [[3](https://github.com/project-thirdeye/thirdeye)], [[4](https://thirdeye.readthedocs.io/en/latest/quick_start.html)]. ThirdEye is a monitoring tool for Anomaly Detection and interactive Root Cause Analysis built in Java. ThirdEye relies on data store such as [Apache Pinot](https://pinot.apache.org/) in order to retrieve data in order to perform the analysis. In this work the aim is to bring the functionalities of ThirdEye in a streaming setting in order to provide insights on the fly at data ingestion, before reaching a data warehouse. The project is developed using [Apache Flink](https://flink.apache.org/) framework which provides a rich API to facilitate stream processing and [Apache Kafka](https://kafka.apache.org/) which serves as the message queue of input data streams that feeds Flink.

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
In this section information about configuring and deploying Flink Jobs will be provided.

## Job configuration
The parameters of a Job can be configured through the `online-ad-rca/src/main/resources/application.conf` file. Details of it can be found

## Generate Job jar file
To generate a new jar that can be submitted to the Flink cluster, navigate to the online-ad-rca project folder and the use `sbt assembly` command. This will produce a new jar named `online-ad-rca/target/scala-2.12/online-ad-rca-{VERSION}.jar`

## Submit Job
The produced Job jar file can be manually uploaded and submitted to the Flink Cluster through the Flink UI.

Alternatively the jar file can be uploaded using the Flink API's [/jars/upload](https://nightlies.apache.org/flink/flink-docs-master/docs/ops/rest_api/#jars-upload) endpoint and then be submitted using [/jars/:jardid/run](https://nightlies.apache.org/flink/flink-docs-master/docs/ops/rest_api/#jars-jarid-run) endpoint for the specific jarid assigned.

Finally, the job can be submitted also by using the Flink CLI using `flink run` command as documentated [here](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/cli/#submitting-a-job). In the case of Docker deployed Flink this has to be handled accordingly.

# References
[1]
StarTree ThirdEye https://dev.startree.ai/docs/startree-enterprise-edition/startree-thirdeye/

[2]
StarTree ThirdEye Community Edition open source project, https://github.com/startreedata/thirdeye

[3]
Archived ThirdEye project (will be deleted by end of 2023), https://github.com/project-thirdeye/thirdeye

[4]
Documentation of archived ThirdEye project, https://thirdeye.readthedocs.io/en/latest/quick_start.html