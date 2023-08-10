DROP TABLE IF EXISTS t_user_info;
CREATE TABLE t_user_info
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_value VARCHAR(255) DEFAULT NULL COMMENT '值',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_user_name(c_user,c_name) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;