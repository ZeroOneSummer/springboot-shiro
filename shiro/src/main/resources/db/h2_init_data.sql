DELETE FROM T_USER;
INSERT INTO T_USER (USER_NAME, PASS_WORD, SALT, ROLES, PERMS) VALUES
('zhang', 123456, 'HAHIAFAWW', 'admin', 'query,delete,update,add'),
('li', 123456, 'HAHIAFAWW', 'teller', 'query,update,add'),
('wang', 123456, 'HAHIAFAWW', 'visitor', 'query')
;