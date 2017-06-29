CREATE TABLE IF NOT EXISTS `testing.test_map`(
  `id` string,
  `type` map<string,bigint>,
  `created_at` string,
  `created_by` string)
PARTITIONED BY (
  `year` string,
  `month` string,
  `month_id` string)
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