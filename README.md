# online-rca

# Data generation
DSGen-software-code-3.2.0rc1/tools/tpcds.sql contains the create statements for the tables of the database
./dsdgen -help

./dsdgen -scale 1 -dir /dsdgen_output/

# Query generation
./dsqgen -help

# Docker
Create entry point script in order to generate data when container is created.

# Creating a custom Makefile
In order to create a custom makefile copy the Makefile.suite file and make changes on the copy created

# Extras
TPC-DS in PostgreSQL [link](https://ankane.org/tpc-ds)


# Requirements
## Kafka Producer

```sh
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASS=postgres
DB_NAME=tpcds

KAFKA_BROKERS=localhost:9093
```

Run producer example:
```
python kafka_producer/sql_to_kafka.py --table_name web_sales_cube1 --topic_name test1 --batch_size 10000 --limit 2000
```

java 11
scala 2.12


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

Python
conda create --name sql_to_kafka python=3.10
conda activate sql_to_kafka
pip install -r requirements.txr
