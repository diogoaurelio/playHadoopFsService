#!/usr/bin/env bash

CONTAINER_NAME=playhadoop

docker rm -f -v $CONTAINER_NAME

docker run -d -t \
 --name playhadoop \
 $CONTAINER_NAME

docker ps

printf "Finished docker container ${CONTAINER_NAME} startup. To enter it: \n docker exec -it ${CONTAINER_NAME} bash \n"