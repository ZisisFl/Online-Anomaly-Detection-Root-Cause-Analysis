#!/bin/bash
SCALE=2
DB_NAME=tpcds$SCALE

read -p "Postgres user password: " -s pass
export PGPASSWORD=$pass;
echo ""

echo "Creating database ${DB_NAME}"

# create database named tpcds
createdb $DB_NAME -h localhost -p 5432 -U postgres -w postgres
# create tables in that database
psql -h localhost -p 5432 -U postgres -w -d $DB_NAME -f dsdgen_output/scale$SCALE/dw_create.sql