#!/bin/sh

SCALE=10
SEED=66
IMAGE_TAG=0.1
IMAGE_NAME=dsdgen${SCALE}:${IMAGE_TAG}
CONTAINER_NAME=dsdgenerator${SCALE}
OUTPUT_FOLDER=dsdgen_output/scale${SCALE}

# build image
docker build --build-arg SCALE=${SCALE} --build-arg SEED=${SEED} -t ${IMAGE_NAME} .

# start container
docker run -d --name ${CONTAINER_NAME} ${IMAGE_NAME}

echo "Copying data from Docker container to local filesystem"
mkdir -p ${OUTPUT_FOLDER}
docker cp ${CONTAINER_NAME}:/dsdgen_output/. ${OUTPUT_FOLDER}/.
docker cp ${CONTAINER_NAME}:/dsgenerator/tools/tpcds.sql ${OUTPUT_FOLDER}/dw_create.sql

echo "Stoping ${CONTAINER_NAME} container"
docker stop ${CONTAINER_NAME}
echo "Deleting ${CONTAINER_NAME} container"
docker rm ${CONTAINER_NAME}