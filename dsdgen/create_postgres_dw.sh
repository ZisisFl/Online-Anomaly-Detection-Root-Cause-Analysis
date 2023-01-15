#!/bin/sh

# create database named tpcds
createdb tpcds -h localhost -p 5432 -U postgres -W postgres
# create tables in that database
psql -h localhost -p 5432 -U postgres -d tpcds -f dsdgen_output/dw_create.sql