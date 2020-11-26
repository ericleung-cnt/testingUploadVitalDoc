DELETE FROM SYSTEM_FUNCS;
GO

SET IDENTITY_INSERT SYSTEM_FUNCS ON;
GO

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(1, 'SR_VIEW', 'Ship Reg View');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(2, 'SR_CREATE', 'Ship Reg Create');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(3, 'SR_UPDATE', 'Ship Reg Update');

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(4, 'MMO_VIEW', 'MMO View');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(5, 'MMO_CREATE', 'MMO Create');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(6, 'MMO_UPDATE', 'MMO Update');

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(7, 'FINANCE_VIEW', 'FINANCE View');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(8, 'FINANCE_CREATE', 'FINANCE Create');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(9, 'FINANCE_UPDATE', 'FINANCE Update');

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(10, 'CODETABLE_VIEW', 'CODETABLE View');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(11, 'CODETABLE_CREATE', 'CODETABLE Create');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(12, 'CODETABLE_UPDATE', 'CODETABLE Update');

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(13, 'RENEW_SEAFARER_REGISTRATION', 'RENEW_SEAFARER_REGISTRATION');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(14, 'RE_ISSUE_SERB', 'RE_ISSUE_SERB');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(15, 'CREATE_DEMAND_NOTE', 'CREATE_DEMAND_NOTE');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(16, 'CANCEL_DEMAND_NOTE', 'CANCEL_DEMAND_NOTE');
INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC) values(17, 'GEN_REPORT', 'GEN_REPORT');

INSERT INTO SYSTEM_FUNCS(FUNC_ID, FUNC_KEY, FUNC_DESC, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values(18, 'DNS_VIEW', 'DNS_VIEW', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP, 0);



UPDATE SYSTEM_FUNCS SET ROWVERSION=0, CREATE_BY='SYSTEM', CREATE_DATE=CURRENT_TIMESTAMP, LASTUPD_BY='SYSTEM', LASTUPD_DATE=CURRENT_TIMESTAMP;

SET IDENTITY_INSERT SYSTEM_FUNCS OFF;
GO


INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(1, 1);
INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(2, 1);
INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(3, 1);
INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(4, 1);
INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(5, 1);
INSERT INTO FUNC_ENTITLES(FUNC_ID, ROLE_ID) VALUES(6, 1);

UPDATE SYSTEM_FUNCS SET ROWVERSION=0, CREATE_BY='SYSTEM', CREATE_DATE=CURRENT_TIMESTAMP, LASTUPD_BY='SYSTEM', LASTUPD_DATE=CURRENT_TIMESTAMP;