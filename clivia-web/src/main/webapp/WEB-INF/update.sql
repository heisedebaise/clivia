-- 2021-07-16;
ALTER TABLE t_user ADD COLUMN c_gesture CHAR(32) DEFAULT NULL COMMENT '手势密码' AFTER c_password;