USE testing; LOAD DATA LOCAL INPATH '/usr/local/mount_data/test_map_data.txt' OVERWRITE INTO TABLE test_map PARTITION (year='2015', month='08', month_id='201508');
USE testing; SELECT COUNT(*) FROM test_map;