DELETE FROM T_USER;
INSERT INTO T_USER (USER_NAME, PASS_WORD, SALT, BANK_NO, ROLES, PERMS) VALUES
('BOC0001', '35b5fcdbb1c7cd29910b90319df30ba3', 'X#@oadeLW&^PhW*u', '105', 'admin', 'query,update,add'),
('BOC0002', '123456', 'xxxx', '105', 'teller', 'query,update,add'),
('lisa', 'b6c53156fbd60163a5d49e3397275945', 'zJYMU*Hq$yHa@rMW', '105', 'visitor', 'query')
;