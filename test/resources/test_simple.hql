USE testing; LOAD DATA LOCAL INPATH '/usr/local/mount_data/test_simple_data.txt' OVERWRITE INTO TABLE test_simple;
USE testing; SELECT COUNT(*) FROM testing.test_simple;