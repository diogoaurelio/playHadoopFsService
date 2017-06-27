#!/usr/bin/env bash

HIVE_PORT=10000

hadoop fs -mkdir -p /tmp
hadoop fs -mkdir -p /user/hive/warehouse
hadoop fs -chmod g+w /tmp
hadoop fs -chmod g+w /user/hive/warehouse

# Start hive server (HS2)
$HIVE_HOME/bin/hiveserver2 &

# Enable beeline
$HIVE_HOME/bin/beeline -u jdbc:hive2://localhost:$HIVE_PORT

# Run HCatalogue
#$HIVE_HOME/hcatalog/sbin/hcat_server.sh &
$HIVE_HOME/hcatalog/bin/hcat &