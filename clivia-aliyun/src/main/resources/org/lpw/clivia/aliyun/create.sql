DROP TABLE IF EXISTS t_aliyun;
CREATE TABLE t_aliyun
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用KEY',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_access_key_id VARCHAR(255) DEFAULT NULL COMMENT 'AccessKey ID',
  c_access_key_secret VARCHAR(255) DEFAULT NULL COMMENT 'AccessKey Secret',
  c_region_id VARCHAR(255) DEFAULT NULL COMMENT '实例区域ID',
  c_endpoint VARCHAR(255) DEFAULT NULL COMMENT '实例区域域名',
  c_instance_name VARCHAR(255) DEFAULT NULL COMMENT '实例名称',
  c_concurrency INT DEFAULT 0 COMMENT '每秒并发数',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key(c_key) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;