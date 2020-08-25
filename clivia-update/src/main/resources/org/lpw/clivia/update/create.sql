DROP TABLE IF EXISTS t_update;
CREATE TABLE t_update
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_version INT DEFAULT 0 COMMENT '版本号',
  c_forced INT DEFAULT 0 COMMENT '强制升级：0-否；1-是',
  c_client VARCHAR(255) DEFAULT NULL COMMENT '客户端：0-Android；1-iOS',
  c_explain TEXT DEFAULT NULL COMMENT '说明',
  c_url VARCHAR(255) DEFAULT NULL COMMENT '升级URL',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;