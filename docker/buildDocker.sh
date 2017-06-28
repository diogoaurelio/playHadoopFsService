#!/usr/bin/env bash

CONTAINER_NAME=playhadoop

docker rm -f -v $CONTAINER_NAME

docker build -t $CONTAINER_NAME .
