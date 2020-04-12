DROP TABLE IF EXISTS t_user_auth;
CREATE TABLE t_user_auth
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) DEFAULT NULL COMMENT '用户ID',
  c_uid VARCHAR(255) NOT NULL COMMENT '认证ID',
  c_type VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_mobile VARCHAR(255) DEFAULT NULL COMMENT '第三方账号手机号',
  c_email VARCHAR(255) DEFAULT NULL COMMENT '第三方账号Email',
  c_nick VARCHAR(255) DEFAULT NULL COMMENT '第三方账号昵称',
  c_portrait VARCHAR(255) DEFAULT NULL COMMENT '第三方头像URL',
  c_time DATETIME DEFAULT NULL COMMENT '绑定时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_uid(c_uid) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_user_auth(c_id,c_user,c_uid) VALUES('00000000-0000-0000-0000-000000000000','00000000-0000-0000-0000-000000000000','system');
INSERT INTO t_user_auth(c_id,c_user,c_uid) VALUES('99999999-9999-9999-9999-999999999999','99999999-9999-9999-9999-999999999999','root');