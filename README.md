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