CREATE DATABASE metastore;
USE metastore;
-- upgraded from hive-schema-0.13.0.mysql.sql --> hive-schema-2.1.0.mysql.sql
SOURCE /usr/local/hive/scripts/metastore/upgrade/mysql/hive-schema-2.1.0.mysql.sql ;
CREATE USER hiveuser@localhost IDENTIFIED BY 'hive_password';
CREATE USER APP@localhost IDENTIFIED BY 'mine';
GRANT ALL PRIVILEGES ON metastore.* TO hiveuser@localhost;
GRANT ALL PRIVILEGES ON metastore.* TO APP@localhost;
FLUSH PRIVILEGES;