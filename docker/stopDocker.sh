#!/usr/bin/env bash

PARAM=$1
# set default val ('playhadoop') if param not provided
CONTAINER_NAME=${PARAM:=playhadoop}

docker rm -f -v $CONTAINER_NAME