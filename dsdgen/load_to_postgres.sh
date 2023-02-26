#!/bin/bash
SCALE=10
DB_NAME=tpcds$SCALE
INPUT_FOLDER=dsdgen_output/scale$SCALE

read -p "Postgres user password: " -s pass
export PGPASSWORD=$pass;
echo ""

echo "Start table creation process"

for i in ${INPUT_FOLDER}/*.dat; do
  filename=$(basename $i)
  table=${filename/.dat/}
  echo "Loading $table..."
  sed 's/|$//' $i > /tmp/$filename
  psql -h localhost -p 5432 -U postgres -w -d $DB_NAME -q -c "TRUNCATE $table"
  psql -h localhost -p 5432 -U postgres -w -d $DB_NAME -c "\\copy $table FROM '/tmp/$filename' CSV DELIMITER '|'"
done