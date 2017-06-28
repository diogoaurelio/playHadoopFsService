CREATE TABLE IF NOT EXISTS `testing.test_simple`(
  `id` string,
  `created_at` string, 
  `created_by` string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\t' 
COLLECTION ITEMS TERMINATED BY ',' 
MAP KEYS TERMINATED BY ':' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
TBLPROPERTIES (
  'schema.checksum'='268A5DEE8D2AC2A757FBB8C5F1265B5C', 
  'transient_lastDdlTime'='1454943496');

LOAD DATA LOCAL INPATH '/usr/local/mount_data/test_simple_data.txt' OVERWRITE INTO TABLE testing.test_simple;
SELECT COUNT(*) FROM testing.test_simple;