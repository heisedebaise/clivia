DROP TABLE IF EXISTS t_pair;
CREATE TABLE t_pair
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_owner VARCHAR(255) NOT NULL COMMENT '归属',
  c_value VARCHAR(255) DEFAULT NULL COMMENT '值',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_owner_value(c_owner,c_value) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;