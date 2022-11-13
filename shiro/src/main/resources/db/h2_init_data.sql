DELETE FROM T_USER;
INSERT INTO T_USER (USER_NAME, PASS_WORD, SALT, BANK_NO, ROLES, PERMS) VALUES
('BOC0001', 123456, 'xxxxxxxx', '105', 'admin', 'query,delete,update,add'),
('BOC0002', 123456, 'xxxxxxxx', '105', 'teller', 'query,update,add'),
('BOC0003', 123456, 'xxxxxxxx', '105', 'visitor', 'query')
;