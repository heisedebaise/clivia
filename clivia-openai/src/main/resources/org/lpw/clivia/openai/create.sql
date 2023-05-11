DROP TABLE IF EXISTS t_openai;
CREATE TABLE t_openai
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT 'KEY',
  c_authorization VARCHAR(255) DEFAULT NULL COMMENT '认证KEY',
  c_organization VARCHAR(255) DEFAULT NULL COMMENT '组织ID',
  c_chat VARCHAR(255) DEFAULT NULL COMMENT '聊天模型',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key(c_key) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;