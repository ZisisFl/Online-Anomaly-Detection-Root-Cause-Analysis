#!/bin/sh

docker build -t dsdgen:0.1 .
docker run -d --name dsdgenerator dsdgen:0.1