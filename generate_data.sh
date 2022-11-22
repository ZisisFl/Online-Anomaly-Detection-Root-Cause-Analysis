#!/bin/bash

# build image
docker build -t dsdgen:0.1 .

# start container
docker run -d --name dsdgenerator dsdgen:0.1

echo "Copying data from Docker container to local filesystem"
mkdir dsdgen_output
docker cp dsdgenerator:/dsdgen_output/. dsdgen_output/.
docker cp dsdgenerator:/dsgenerator/tools/tpcds.sql dsdgen_output/dw_create.sql

echo "Stoping dsdgenerator container"
docker stop dsdgenerator
echo "Deleting dsdgenerator container"
docker rm dsdgenerator