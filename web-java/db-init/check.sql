SET PAGESIZE 50
WHENEVER SQLERROR EXIT FAILURE
CONNECT system/Oracle123456@localhost:1521/FREEPDB1

PROMPT === USER ===
SELECT username, created FROM dba_users WHERE username LIKE 'SUCO%' OR username LIKE 'U_DEMO';

PROMPT === OBJECT POWNSHIP CUA SUCO_APP ===
SELECT object_type, count(*) cnt FROM dba_objects WHERE owner='SUCO_APP' GROUP BY object_type ORDER BY object_type;

PROMPT === 10 BANG DAU TIEN ===
SELECT table_name FROM dba_tables WHERE owner='SUCO_APP' ORDER BY 1 FETCH FIRST 10 ROWS ONLY;

PROMPT === SO DONG BANG SU_CO (neu co) ===
SELECT count(*) FROM SUCO_APP.SU_CO;

EXIT SUCCESS
