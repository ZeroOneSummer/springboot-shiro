DELETE FROM T_USER;
INSERT INTO T_USER (USER_NAME, PASS_WORD, SALT, ROLES, PERMS) VALUES
('BOC0001', 123456, 'xxxxxxxx', 'admin', 'query,delete,update,add'),
('BOC0002', 123456, 'xxxxxxxx', 'teller', 'query,update,add'),
('BOC0003', 123456, 'xxxxxxxx', 'visitor', 'query')
;