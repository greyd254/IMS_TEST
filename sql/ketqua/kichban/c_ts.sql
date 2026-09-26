SET DEFINE OFF ECHO ON LINESIZE 220 PAGESIZE 60 FEEDBACK ON TRIMSPOOL ON
COLUMN file_name FORMAT A45
COLUMN tablespace_name FORMAT A16
COLUMN name FORMAT A45
SPOOL C:\Users\GreyD\IdeaProjects\IMS\sql\ketqua\03_tablespace_datafile.log
ALTER SESSION SET CONTAINER=FREEPDB1;

PROMPT === 1. Truy van thong tin tablespace va datafile hien co (co TS_SUCO cua du an)
SELECT tablespace_name, status, contents, extent_management, segment_space_management FROM dba_tablespaces ORDER BY 1;
SELECT file_name, tablespace_name, ROUND(bytes/1024/1024) AS MB, autoextensible, ROUND(increment_by*8192/1024/1024) AS NEXT_MB, ROUND(maxbytes/1024/1024) AS MAX_MB FROM dba_data_files ORDER BY tablespace_name;
SELECT tablespace_name, ROUND(SUM(bytes)/1024/1024,1) AS FREE_MB FROM dba_free_space GROUP BY tablespace_name ORDER BY 1;

PROMPT === 2. THEM tablespace TS_DEMO (datafile 20M, tu mo rong)
CREATE SMALLFILE TABLESPACE TS_DEMO DATAFILE 'C:\ORADATA\ts_demo01.dbf' SIZE 20M AUTOEXTEND ON NEXT 5M MAXSIZE 100M;
SELECT file_name, ROUND(bytes/1024/1024) AS MB, autoextensible FROM dba_data_files WHERE tablespace_name='TS_DEMO';

PROMPT === 3. SUA: them datafile thu hai
ALTER TABLESPACE TS_DEMO ADD DATAFILE 'C:\ORADATA\ts_demo02.dbf' SIZE 10M;

PROMPT === 4. SUA: doi kich thuoc datafile (RESIZE 20M -> 30M)
ALTER DATABASE DATAFILE 'C:\ORADATA\ts_demo01.dbf' RESIZE 30M;

PROMPT === 5. BAT / TAT tu dong mo rong (AUTOEXTEND) cua 1 datafile
ALTER DATABASE DATAFILE 'C:\ORADATA\ts_demo02.dbf' AUTOEXTEND ON NEXT 5M MAXSIZE 100M;
SELECT file_name, autoextensible, ROUND(maxbytes/1024/1024) AS MAX_MB FROM dba_data_files WHERE tablespace_name='TS_DEMO' ORDER BY 1;
ALTER DATABASE DATAFILE 'C:\ORADATA\ts_demo02.dbf' AUTOEXTEND OFF;
SELECT file_name, autoextensible FROM dba_data_files WHERE tablespace_name='TS_DEMO' ORDER BY 1;

PROMPT === 6. SUA: doi ten tablespace TS_DEMO -> TS_DEMO_MOI
ALTER TABLESPACE TS_DEMO RENAME TO TS_DEMO_MOI;
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name LIKE 'TS_DEMO%';

PROMPT === 7. Tao bang co du lieu trong tablespace de chung minh du lieu con nguyen sau khi di chuyen
CREATE TABLE SYSTEM.DEMO_TS (ID NUMBER, GHI_CHU VARCHAR2(50)) TABLESPACE TS_DEMO_MOI;
INSERT INTO SYSTEM.DEMO_TS VALUES (1, 'du lieu truoc khi di chuyen datafile');
COMMIT;

PROMPT === 8a. Thu DI CHUYEN datafile ONLINE (ALTER DATABASE MOVE DATAFILE) - ban Free khong ho tro, phai loi ORA-00439
ALTER DATABASE MOVE DATAFILE 'C:\ORADATA\ts_demo02.dbf' TO 'C:\ORADATA\demo_move\ts_demo02.dbf';

PROMPT === 8b. DI CHUYEN datafile theo cach OFFLINE: offline tablespace -> copy file bang HDH -> RENAME DATAFILE -> online
ALTER TABLESPACE TS_DEMO_MOI OFFLINE NORMAL;
HOST copy C:\ORADATA\ts_demo02.dbf C:\ORADATA\demo_move\ts_demo02.dbf
ALTER TABLESPACE TS_DEMO_MOI RENAME DATAFILE 'C:\ORADATA\ts_demo02.dbf' TO 'C:\ORADATA\demo_move\ts_demo02.dbf';
ALTER TABLESPACE TS_DEMO_MOI ONLINE;
HOST del C:\ORADATA\ts_demo02.dbf
SELECT file_name, tablespace_name FROM dba_data_files WHERE tablespace_name='TS_DEMO_MOI' ORDER BY 1;
SELECT * FROM SYSTEM.DEMO_TS;

PROMPT === 9. OFFLINE / ONLINE tablespace
ALTER TABLESPACE TS_DEMO_MOI OFFLINE NORMAL;
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name='TS_DEMO_MOI';
SELECT * FROM SYSTEM.DEMO_TS;
ALTER TABLESPACE TS_DEMO_MOI ONLINE;
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name='TS_DEMO_MOI';

PROMPT === 10. READ ONLY / READ WRITE tablespace
ALTER TABLESPACE TS_DEMO_MOI READ ONLY;
INSERT INTO SYSTEM.DEMO_TS VALUES (2, 'ghi khi READ ONLY (phai loi)');
ALTER TABLESPACE TS_DEMO_MOI READ WRITE;
INSERT INTO SYSTEM.DEMO_TS VALUES (2, 'ghi khi READ WRITE');
COMMIT;
SELECT * FROM SYSTEM.DEMO_TS ORDER BY ID;

PROMPT === 11. Truy van thong tin sau cac thay doi (dba_data_files, v$datafile, dba_free_space, dba_segments)
SELECT file_name, ROUND(bytes/1024/1024) AS MB, autoextensible FROM dba_data_files WHERE tablespace_name='TS_DEMO_MOI' ORDER BY 1;
SELECT name, status, enabled FROM v$datafile WHERE name LIKE 'C:\ORADATA%' ORDER BY 1;
SELECT tablespace_name, ROUND(SUM(bytes)/1024/1024,1) AS FREE_MB FROM dba_free_space WHERE tablespace_name='TS_DEMO_MOI' GROUP BY tablespace_name;
SELECT owner, segment_name, segment_type, tablespace_name FROM dba_segments WHERE tablespace_name='TS_DEMO_MOI';

PROMPT === 12. XOA tablespace cung noi dung va file tren dia
DROP TABLE SYSTEM.DEMO_TS PURGE;
DROP TABLESPACE TS_DEMO_MOI INCLUDING CONTENTS AND DATAFILES;
SELECT COUNT(*) AS TS_DEMO_CON_LAI FROM dba_tablespaces WHERE tablespace_name LIKE 'TS_DEMO%';
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name='TS_SUCO';
SPOOL OFF
EXIT
