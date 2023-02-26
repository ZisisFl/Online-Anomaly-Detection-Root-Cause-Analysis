#!/bin/sh

# simple create a docker container of dsdgenerator with default arguments and run it
docker build -t dsdgen:0.1 .
docker run -d --name dsdgenerator dsdgen:0.1