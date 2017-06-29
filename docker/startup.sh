#!/usr/bin/env bash

HIVE_PORT=10000
HIVE_SERVER_PORT=10002
MNT_DIR=/usr/local/mount_data
DB_NAME=testing

nohup service mysql start > /tmp/mysql.txt &

hadoop fs -mkdir -p /tmp
hadoop fs -mkdir -p /tmp/hive
hadoop fs -mkdir -p /user/hive/warehouse
hadoop fs -chmod g+w /tmp
hadoop fs -chmod g+w /user/hive/warehouse
hadoop fs -chmod g+w /user/hive/
hadoop fs -chmod g+w /tmp/hive/
hadoop fs -chmod -R 777 /tmp/hive/
hadoop fs -chmod -R 777 /user/hive/warehouse
hadoop fs -chmod -R a+rwx /user/hive/warehouse
chmod -R a+rwx /user/hive/warehouse


# make sure mysql has had enough time to startup

# Start metastore
#nohup $HIVE_HOME/bin/hive --service metastore > /tmp/hive_mestore_boot.txt &

# make sure hcat_server.sh script can start ...
mkdir -p /usr/local/hive/hcatalog/var/log
# Start metastore + HCatalogue
nohup $HIVE_HOME/hcatalog/sbin/hcat_server.sh start > /tmp/hcatalog_boot.txt &



sleep 3

# Start hive server (HS2)
nohup $HIVE_HOME/bin/hiveserver2 > /tmp/hiveserver2_boot.txt &

# Run scripts

$HIVE_HOME/hcatalog/bin/hcat -e "CREATE DATABASE IF NOT EXISTS $DB_NAME" &
sleep 5

if [ -f /usr/local/mount_data/test_map.hql ]; then
    echo "File was correctly mounted. Creating tables for DB '$DB_NAME'"
    $HIVE_HOME/hcatalog/bin/hcat -f '/usr/local/mount_data/create_table_test_simple.hql' &
    $HIVE_HOME/hcatalog/bin/hcat -f '/usr/local/mount_data/create_table_test_map.hql' &

    sleep 5
    echo "Loading data into tables...";
    { $HIVE_HOME/bin/hive -f '/usr/local/mount_data/test_simple.hql' & } || { echo "An error might have occured while loading data into test_simple table."; }
    { $HIVE_HOME/bin/hive -f '/usr/local/mount_data/test_map.hql' & } || { echo "An error might have occured while loading data into test_map table."; }
else
    echo "Test resources files appear NOT to have been correctly mounted!"
fi

# beeline
#nohup $HIVE_HOME/bin/beeline -u jdbc:hive2://localhost:$HIVE_PORT > /tmp/beeline_boot.txt &

#sleep 3
#nohup $HIVE_HOME/hcatalog/bin/hcat start > /tmp/hcatalog_boot.txt &

sleep 10
if [ -f /usr/local/mount_data/test_map.hql ]; then
    echo "Done loading data into tables"
fi

hadoop fs -chmod -R 777 /user/hive/warehouse

/bin/bash