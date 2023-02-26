# Overview
This directory contains a Docker image that creates an ubuntu instance which contains the source of the [TPC-DS tool](https://www.tpc.org/tpcds/) and its required dependencies in order to run it. By building the image it generates data of a specific `scale` which is a build argument.

# Instructions
This section includes
## TPC-DS tool downloading
In order to create a Docker image of the TPC-DS tool you first need to download it by filling the required form in the [website](https://www.tpc.org/tpcds/).

After you download the TPC-DS zip file you need to:
- Unzip the downloaded file
- Put the unziped folder under in ./dsdgen directory
- Rename the folder to DSGen-software-code

By doing so the DSGen-software-code folder will be grayed out in your IDE as it is part of the ignored directories in the .gitignore file.

## Running TPC-DS generator as Docker container
To run the TPC-DS generator and copy the ouput data of the dsdgen tool to a directory of the host machine, a bash script was created named `generate_data.sh`. This script builds the Docker image, runs it with the specified build arguments, generates the required data artifacts and then stops and removes the container created. By editing the script you can change the `SCALE` and the `SEED` of the data as well as the tag for the Docker container. The execution of this script generates the required data artifacts in a directory according to the following format `dsdgen/dsdgen_output/scale{$SCALE_CHOSEN}`

To run the script use the following command:
```bash
./generate_data.sh
```

## Loading data to PostgreSQL
Next step is to load the data to a PostgreSQL database. You can also use a SQL database of your preference by altering the scripts provided or by executing the steps manually. This section assumes you have a PostgreSQL instance available at localhost:5432.

To set up a database for TPC-DS data run the following command:
```bash
./create_postgres_dw.sh
```
This script will create a database named `tpcds{$SCALE}` and create the database schema found in the `dsdgen_output/scale{$SCALE}/dw_create.sql` file.

Once you have the database set up, the next step is to load the actual data in the tables created. For this task you can use the `load_to_postgres.sh` script. You will also need to configure data of which scale need to be used as in all previous scripts. 
```bash
./load_to_postgres.sh
```

After that you can create data cubes in the form of views out of the database tables that will be fed to Kafka using `kafka_producer`. You can find some views that are used in the experiments in the dw directory.

# Extras
- TPC-DS in PostgreSQL [link](https://ankane.org/tpc-ds)
- Accessing tool for query generation inside container of TPC-DS ./dsqgen -help
- Accessing tool for data generation inside container of TPC-DS ./dsdgen -help