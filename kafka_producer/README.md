# Overview
This tool is a Kafka producer written in Python using [confluent-kafka-python](https://github.com/confluentinc/confluent-kafka-python) client library. The producer is required in order to records from data cubes created in SQL data warehouse of TPC-DS data to Kafka in the form of JSON records (each table row becomes a JSON record in Kafka). The tool works with any SQLAlchemy compatible SQL database, in my case I was using PostgreSQL.

# Prerequisites
## Python
In order to use the Kafka producer you are advised to create a Python virtual environment in order to install the required libraries.

Here we provide an example of creating the Python environment using Anaconda you can use the package manager of your preference
```bash
conda create --name sql_kafka_producer python=3.10
conda activate sql_kafka_producer
pip install -r requirements.txt
```

## Environment
You will also need to create a `.env` file containing the required environmental variables in order to connect with the TPC-DS SQL database and the sink Kafka Broker.
```sh
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASS=postgres
DB_NAME=tpcds

KAFKA_BROKERS=localhost:9093
```

# Run
Run producer fetching data 

In order to review the available options of the cli tool you can run the following command
```bash
python sql_to_kafka.py -h
```

## Example scenario
Generating 2000 records in topic test1 with data from table web_sales_cube1:
```
python sql_to_kafka.py --table_name web_sales_cube1 --topic_name test1 --batch_size 10000 --limit 2000
```