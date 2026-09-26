-- =====================================================================
-- 07_admin_demo.sql — KỊCH BẢN DEMO QUẢN TRỊ ORACLE
-- !!! KHÔNG chạy cả file một lần. Chạy THỦ CÔNG từng khối, đọc comment trước.
-- Mỗi khối ghi rõ: chạy bằng user nào, ở CDB (CDB$ROOT) hay PDB (FREEPDB1).
-- Kết nối mẫu:
--   CDB$ROOT : sqlplus / as sysdba                     (hoặc sys@localhost:1521/FREE as sysdba)
--   PDB      : sqlplus sys@localhost:1521/FREEPDB1 as sysdba
-- Tablespace demo TS_DEMO KHÔNG dính tới TS_SUCO, SYSTEM hay SYSAUX.
-- =====================================================================
SET LINESIZE 220
SET PAGESIZE 100
COLUMN NAME FORMAT A20
COLUMN FILE_NAME FORMAT A80
COLUMN TABLESPACE_NAME FORMAT A20

-- Thư mục chứa datafile của PDB. Xem đường dẫn thật bằng câu truy vấn ở khối 2.1 rồi SỬA dòng dưới
-- (bắt buộc kết thúc bằng dấu / hoặc \). Ví dụ Windows: C:\APP\ORADATA\FREE\FREEPDB1\
-- (Bản đã chạy thử dùng C:\ORADATA\ vì thư mục datafile mặc định của Oracle không cho user thường xóa file.)
DEFINE dir = 'C:\ORADATA\'
-- Thư mục đích khi di chuyển datafile (phải tồn tại sẵn, Oracle có quyền ghi)
DEFINE dir2 = 'C:\ORADATA\demo_move\'

-- #####################################################################
-- PHẦN 1. INSTANCE VÀ PDB
-- #####################################################################

-- 1.1 [SYSDBA, CDB$ROOT] Xem trạng thái instance
SELECT instance_name, status, database_status, startup_time FROM v$instance;

-- 1.2 [SYSDBA, CDB$ROOT] Xem các PDB và chế độ mở
SELECT con_id, name, open_mode, restricted FROM v$pdbs;

-- 1.3 [SYSDBA, CDB$ROOT] TẮT instance (toàn bộ DB, mọi PDB sẽ đóng theo). Phiên đang chạy bị rollback.
--     Sau lệnh này phải chạy STARTUP, các câu SELECT ở trên không dùng được cho tới khi khởi động lại.
-- SHUTDOWN IMMEDIATE

-- 1.4 [SYSDBA, CDB$ROOT] Khởi động lại instance (MOUNT -> OPEN)
-- STARTUP
-- Lưu ý: PDB có thể không tự mở. Nếu FREEPDB1 đang MOUNTED thì chạy 1.6.
-- (Muốn PDB tự mở lần sau: ALTER PLUGGABLE DATABASE FREEPDB1 SAVE STATE;)

-- 1.5 [SYSDBA, CDB$ROOT] Đóng PDB
-- ALTER PLUGGABLE DATABASE FREEPDB1 CLOSE IMMEDIATE;

-- 1.6 [SYSDBA, CDB$ROOT] Mở PDB chỉ đọc (để sao lưu/kiểm tra, không ghi được)
-- ALTER PLUGGABLE DATABASE FREEPDB1 OPEN READ ONLY;
-- Đổi sang đọc/ghi: đóng rồi mở lại
-- ALTER PLUGGABLE DATABASE FREEPDB1 CLOSE IMMEDIATE;
-- ALTER PLUGGABLE DATABASE FREEPDB1 OPEN READ WRITE;
SELECT name, open_mode FROM v$pdbs;

-- 1.7 [SYSDBA, PDB FREEPDB1] Bật chế độ RESTRICTED SESSION: chỉ user có quyền RESTRICTED SESSION
--     (vd DBA) mới đăng nhập được. Dùng khi bảo trì. Nhớ TẮT lại sau khi xong.
ALTER SESSION SET CONTAINER = FREEPDB1;
ALTER SYSTEM ENABLE RESTRICTED SESSION;
SELECT logins FROM v$instance;     -- RESTRICTED
ALTER SYSTEM DISABLE RESTRICTED SESSION;
SELECT logins FROM v$instance;     -- ALLOWED

-- #####################################################################
-- PHẦN 2. TABLESPACE VÀ DATAFILE  (tất cả chạy bằng SYSDBA hoặc SYSTEM, trong PDB FREEPDB1)
-- #####################################################################
ALTER SESSION SET CONTAINER = FREEPDB1;

-- 2.1 Xem tablespace, datafile và dung lượng trống
SELECT tablespace_name, status, contents, extent_management FROM dba_tablespaces ORDER BY 1;
SELECT file_name, tablespace_name, ROUND(bytes/1024/1024) AS MB, autoextensible,
       ROUND(maxbytes/1024/1024) AS MAX_MB
  FROM dba_data_files ORDER BY tablespace_name;
SELECT tablespace_name, ROUND(SUM(bytes)/1024/1024, 1) AS FREE_MB
  FROM dba_free_space GROUP BY tablespace_name ORDER BY 1;

-- 2.2 Tạo tablespace demo TS_DEMO (20M). Phải ghi rõ SMALLFILE: mặc định của PDB này là BIGFILE
--     (chỉ được 1 datafile, nên 2.3 ADD DATAFILE sẽ lỗi ORA-32771).
CREATE SMALLFILE TABLESPACE TS_DEMO DATAFILE '&dir.ts_demo01.dbf' SIZE 20M AUTOEXTEND ON NEXT 5M MAXSIZE 100M;

-- 2.3 Thêm datafile thứ hai cho TS_DEMO
ALTER TABLESPACE TS_DEMO ADD DATAFILE '&dir.ts_demo02.dbf' SIZE 10M;

-- 2.4 Resize datafile (tăng lên 30M; chỉ giảm được nếu phần cuối file chưa dùng)
ALTER DATABASE DATAFILE '&dir.ts_demo01.dbf' RESIZE 30M;

-- 2.5 Bật / tắt AUTOEXTEND
ALTER DATABASE DATAFILE '&dir.ts_demo02.dbf' AUTOEXTEND ON NEXT 5M MAXSIZE 100M;
ALTER DATABASE DATAFILE '&dir.ts_demo02.dbf' AUTOEXTEND OFF;

-- 2.5b Đổi tên tablespace (từ đây gọi là TS_DEMO_MOI; các khối sau đổi tên tương ứng nếu chạy theo)
ALTER TABLESPACE TS_DEMO RENAME TO TS_DEMO_MOI;

-- 2.6 Di chuyển datafile. Cần thư mục đích &dir2 đã tồn tại.
--     a) Cách ONLINE: KHÔNG dùng được trên Oracle Free, báo ORA-00439 (feature not enabled: online move datafile).
--        Trên bản Enterprise thì chạy được:
-- ALTER DATABASE MOVE DATAFILE '&dir.ts_demo02.dbf' TO '&dir2.ts_demo02.dbf';
--     b) Cách OFFLINE (dùng được ở mọi bản, đã chạy thử thành công):
ALTER TABLESPACE TS_DEMO_MOI OFFLINE NORMAL;
HOST copy &dir.ts_demo02.dbf &dir2.ts_demo02.dbf
ALTER TABLESPACE TS_DEMO_MOI RENAME DATAFILE '&dir.ts_demo02.dbf' TO '&dir2.ts_demo02.dbf';
ALTER TABLESPACE TS_DEMO_MOI ONLINE;
HOST del &dir.ts_demo02.dbf
SELECT file_name FROM dba_data_files WHERE tablespace_name = 'TS_DEMO_MOI';

-- Lưu ý: nếu đã chạy 2.5b thì từ 2.7 trở đi (kể cả 2.8, Phần 3) thay TS_DEMO bằng TS_DEMO_MOI.
-- 2.7 Đưa tablespace OFFLINE / ONLINE (OFFLINE NORMAL: dữ liệu vẫn nguyên, không cần recover)
ALTER TABLESPACE TS_DEMO OFFLINE NORMAL;
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name = 'TS_DEMO';
ALTER TABLESPACE TS_DEMO ONLINE;

-- 2.8 XÓA tablespace demo cùng file trên đĩa. Chỉ dùng cho TS_DEMO, TUYỆT ĐỐI KHÔNG với TS_SUCO/SYSTEM.
--     (Làm ở cuối buổi demo, sau Phần 3, vì Phần 3 cần TS_DEMO.)
-- DROP TABLESPACE TS_DEMO INCLUDING CONTENTS AND DATAFILES;

-- #####################################################################
-- PHẦN 3. KHÔI PHỤC DATAFILE BỊ MẤT BẰNG RMAN (chỉ trên TS_DEMO)
-- #####################################################################
-- ĐIỀU KIỆN CẦN:
--   (a) DB chạy chế độ ARCHIVELOG (kiểm tra: SELECT log_mode FROM v$database;).
--       Nếu là NOARCHIVELOG thì bật (tắt DB tạm thời, ảnh hưởng toàn CDB).
--       Cần có Fast Recovery Area (nơi chứa archive log và backup RMAN), thư mục phải tồn tại sẵn:
--         [SYSDBA, CDB$ROOT]  ALTER SYSTEM SET db_recovery_file_dest_size=5G SCOPE=BOTH;
--                             ALTER SYSTEM SET db_recovery_file_dest='C:\ORADATA\fra' SCOPE=BOTH;
--         [SYSDBA, CDB$ROOT]  SHUTDOWN IMMEDIATE
--                             STARTUP MOUNT
--                             ALTER DATABASE ARCHIVELOG;
--                             ALTER DATABASE OPEN;
--   (b) ĐÃ CÓ BACKUP của TS_DEMO trước khi mất file. Không có backup thì không khôi phục được.
--   (c) Đã có TS_DEMO (Phần 2.2) và dữ liệu thử.
--
-- (Kịch bản dưới đây ĐÃ được chạy thử thành công trên Oracle 23.26.3 Free: sau RESTORE + RECOVER,
--  cả dòng dữ liệu thêm TRƯỚC và SAU lúc backup đều còn nguyên. Kết nối SYSDBA bằng xác thực hệ điều hành:
--  đặt ORACLE_HOME, ORACLE_SID=FREE rồi chạy  sqlplus / as sysdba  và  rman target / .
--  Nên đặt datafile TS_DEMO ở thư mục bạn có quyền xóa file, ví dụ C:\ORADATA\ts_demo01.dbf.)
--
-- BƯỚC 1 [SYSTEM/SYSDBA, PDB]: tạo dữ liệu thử trong TS_DEMO
--   CREATE TABLE SUCO_APP.DEMO_KHOI_PHUC (ID NUMBER, GHI_CHU VARCHAR2(50)) TABLESPACE TS_DEMO;
--   (SUCO_APP có QUOTA trên TS_DEMO? Nếu không: ALTER USER SUCO_APP QUOTA 10M ON TS_DEMO; hoặc
--    tạo bằng SYSTEM: CREATE TABLE SYSTEM.DEMO_KHOI_PHUC (...) TABLESPACE TS_DEMO;)
--   INSERT INTO SYSTEM.DEMO_KHOI_PHUC VALUES (1, 'truoc khi mat file'); COMMIT;
--
-- BƯỚC 2 [RMAN, kết nối CDB root]: chạy trong cửa sổ lệnh:  rman target /
--   BACKUP TABLESPACE FREEPDB1:TS_DEMO;
--   LIST BACKUP OF TABLESPACE FREEPDB1:TS_DEMO;
--
-- BƯỚC 3 [SYSDBA, PDB]: mô phỏng sự cố. Đưa tablespace offline rồi XÓA file .dbf bằng hệ điều hành
--   ALTER TABLESPACE TS_DEMO OFFLINE IMMEDIATE;   -- cần ARCHIVELOG
--   (dùng Explorer / lệnh del xóa đúng file ts_demo01.dbf của TS_DEMO)
--   Truy vấn bảng DEMO_KHOI_PHUC lúc này sẽ lỗi ORA-00376 / ORA-01110.
--
-- BƯỚC 4 [RMAN, CDB root]: khôi phục và áp dụng redo
--   RESTORE TABLESPACE FREEPDB1:TS_DEMO;
--   RECOVER TABLESPACE FREEPDB1:TS_DEMO;
--   (Với RESTORE cần PDB đang mở; nếu RMAN yêu cầu, mở PDB: ALTER PLUGGABLE DATABASE FREEPDB1 OPEN;)
--
-- BƯỚC 5 [SYSDBA, PDB]: đưa tablespace online và kiểm tra dữ liệu còn nguyên
--   ALTER TABLESPACE TS_DEMO ONLINE;
--   SELECT * FROM SYSTEM.DEMO_KHOI_PHUC;
--
-- Dọn dẹp: DROP TABLE SYSTEM.DEMO_KHOI_PHUC PURGE;  rồi DROP TABLESPACE ở 2.8.

-- #####################################################################
-- PHẦN 4. EXPORT / IMPORT SCHEMA BẰNG DATA PUMP (expdp / impdp)
-- #####################################################################
-- 4.1 [SYSTEM, PDB FREEPDB1] Tạo DIRECTORY trỏ tới thư mục đã tồn tại trên máy chủ DB
--     và cấp quyền đọc ghi cho SUCO_APP. (Tạo sẵn thư mục C:\dpdump trước.)
-- CREATE OR REPLACE DIRECTORY DP_DIR AS 'C:\dpdump';
-- GRANT READ, WRITE ON DIRECTORY DP_DIR TO SUCO_APP;
-- GRANT DATAPUMP_EXP_FULL_DATABASE, DATAPUMP_IMP_FULL_DATABASE TO SYSTEM;   -- thường đã có sẵn
--
-- 4.2 [Cửa sổ lệnh Windows, KHÔNG phải trong SQL*Plus] EXPORT schema SUCO_APP:
--   expdp SUCO_APP/SuCo_App_123@localhost:1521/FREEPDB1 schemas=SUCO_APP directory=DP_DIR dumpfile=suco_app.dmp logfile=exp_suco_app.log
--
-- 4.3a [SYSTEM, PDB] PHẢI tạo user đích trước, vì dump export bởi SUCO_APP không chứa lệnh tạo user
--      (thiếu bước này impdp báo ORA-01918 user 'SUCO_APP2' does not exist):
-- CREATE USER SUCO_APP2 IDENTIFIED BY "SuCo_App2_123" DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;
-- GRANT CREATE SESSION TO SUCO_APP2;
-- 4.3b IMPORT sang schema mới SUCO_APP2 (REMAP_SCHEMA đổi chủ sở hữu):
--   impdp SYSTEM/<mat_khau_system>@localhost:1521/FREEPDB1 directory=DP_DIR dumpfile=suco_app.dmp logfile=imp_suco_app2.log remap_schema=SUCO_APP:SUCO_APP2 remap_tablespace=TS_SUCO:USERS
--   (remap_tablespace dùng USERS để không phải tạo lại TS_SUCO; hoặc bỏ tham số này nếu muốn dùng TS_SUCO)
--
-- 4.4 [SYSTEM, PDB] Kiểm tra sau khi import:
-- SELECT owner, table_name FROM dba_tables WHERE owner = 'SUCO_APP2' ORDER BY 2;
-- SELECT COUNT(*) FROM SUCO_APP2.SU_CO;
-- Xóa schema thử: DROP USER SUCO_APP2 CASCADE;
