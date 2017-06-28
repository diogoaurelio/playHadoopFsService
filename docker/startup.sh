#!/usr/bin/env bash

HIVE_PORT=10000
HIVE_SERVER_PORT=10002
MNT_DIR=/usr/local/mount_data

nohup service mysql start > /tmp/mysql.txt &

hadoop fs -mkdir -p /tmp
hadoop fs -mkdir -p /user/hive/warehouse
hadoop fs -chmod g+w /tmp
hadoop fs -chmod g+w /user/hive/warehouse

# make sure mysql has had enough time to startup
sleep 3
# Start metastore
nohup $HIVE_HOME/bin/hive --service metastore > /tmp/hive_mestore_boot.txt &

# Run HCatalogue
#$HIVE_HOME/hcatalog/sbin/hcat_server.sh &


sleep 3

# Start hive server (HS2)
nohup $HIVE_HOME/bin/hiveserver2 > /tmp/hiveserver2_boot.txt &

# Run scripts

if [ -f /usr/local/mount_data/test_map.hql ]; then
    echo "File was correctly mounted. Attempted to setup db and tables"
    $HIVE_HOME/hcatalog/bin/hcat -e "CREATE DATABASE IF NOT EXISTS testing" &
    $HIVE_HOME/hcatalog/bin/hcat -f '/usr/local/mount_data/test_simple.hql' &
    # Tests
#    hadoop fs -cp $MNT_DIR/sample.txt /tmp/sample.txt
#    $HIVE_HOME/hcatalog/bin/hcat -e "CREATE TABLE IF NOT EXISTS employee ( eid int, name String, salary String, destination String) COMMENT 'Employee details' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\T' LINES TERMINATED BY '\n' STORED AS TEXTFILE;"
#    $HIVE_HOME/hcatalog/bin/hcat -e "LOAD DATA LOCAL INPATH '/tmp/sample.txt' OVERWRITE INTO TABLE employee;"
#    $HIVE_HOME/hcatalog/bin/hcat -e "INSERT INTO table employee VALUES('1201', 'Gopal', '45000', 'Technical manager');"

else
    echo "Test resources files appear NOT to have been correctly mounted!"
fi

# beeline
#nohup $HIVE_HOME/bin/beeline -u jdbc:hive2://localhost:$HIVE_PORT > /tmp/beeline_boot.txt &

#sleep 3
#nohup $HIVE_HOME/hcatalog/bin/hcat start > /tmp/hcatalog_boot.txt &