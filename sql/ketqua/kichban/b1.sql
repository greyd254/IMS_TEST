SET DEFINE OFF ECHO ON LINESIZE 200 PAGESIZE 50 FEEDBACK ON TRIMSPOOL ON
SPOOL C:\Users\GreyD\IdeaProjects\IMS\sql\ketqua\02_restricted_session.log
PROMPT === 1. Bat RESTRICTED SESSION trong PDB FREEPDB1 (SYSDBA)
ALTER SESSION SET CONTAINER=FREEPDB1;
SELECT logins FROM v$instance;
ALTER SYSTEM ENABLE RESTRICTED SESSION;
SELECT logins FROM v$instance;
PROMPT === 2. Thu dang nhap bang SUCO_APP (khong co quyen RESTRICTED SESSION) - xem ket qua o buoc tiep theo
HOST java -cp C:\Users\GreyD\.m2\repository\com\oracle\database\jdbc\ojdbc17\23.26.3.0.0\ojdbc17-23.26.3.0.0.jar C:\Users\GreyD\AppData\Local\Temp\claude\C--Users-GreyD-IdeaProjects-IMS\09079b2d-702f-485b-ad17-bcc7c0ec36b6\scratchpad\RunSql.java SUCO_APP SuCo_App_123 C:\Users\GreyD\AppData\Local\Temp\claude\C--Users-GreyD-IdeaProjects-IMS\09079b2d-702f-485b-ad17-bcc7c0ec36b6\scratchpad\c2.sql > C:\Users\GreyD\IdeaProjects\IMS\sql\ketqua\02b_dang_nhap_thu.log 2>&1
PROMPT === 3. Nguoi co quyen RESTRICTED SESSION (SYSDBA) van dang nhap duoc; tat che do nay
ALTER SYSTEM DISABLE RESTRICTED SESSION;
SELECT logins FROM v$instance;
SPOOL OFF
EXIT
