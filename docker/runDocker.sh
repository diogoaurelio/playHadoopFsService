#!/usr/bin/env bash

CONTAINER_NAME=playhadoop
HIVE_PORT=10000
HIVE_METASTORE_PORT=9083
HIVE_SERVER_PORT=10002
DOCKER_INNER_MOUNT=/usr/local/mount_data
RESOURCES_DIR="$(dirname "${PWD}")/test/resources"
echo $RESOURCES_DIR

docker rm -f -v $CONTAINER_NAME



docker run -d -t \
 --name playhadoop \
 -p $HIVE_METASTORE_PORT:$HIVE_METASTORE_PORT \
 -p $HIVE_PORT:$HIVE_PORT \
 -p $HIVE_SERVER_PORT:$HIVE_SERVER_PORT \
 -v $RESOURCES_DIR:$DOCKER_INNER_MOUNT \
 $CONTAINER_NAME

docker ps

printf "Finished docker container ${CONTAINER_NAME} startup. To enter it: \n docker exec -it ${CONTAINER_NAME} bash \n"