#!/bin/bash
read -p "Postgres user password: " -s pass
export PGPASSWORD=$pass;
echo ""

echo "Start table creation process"

for i in dsdgen_output/*.dat; do
  filename=$(basename $i)
  table=${filename/.dat/}
  echo "Loading $table..."
  sed 's/|$//' $i > /tmp/$filename
  psql -h localhost -p 5432 -U postgres -w -d tpcds -q -c "TRUNCATE $table"
  psql -h localhost -p 5432 -U postgres -w -d tpcds -c "\\copy $table FROM '/tmp/$filename' CSV DELIMITER '|'"
done