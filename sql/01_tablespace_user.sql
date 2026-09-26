-- =====================================================================
-- 01_tablespace_user.sql
-- CHẠY BẰNG: SYSTEM, kết nối vào PDB:  sqlplus system@localhost:1521/FREEPDB1
-- MỤC ĐÍCH : tạo tablespace TS_SUCO, user SUCO_APP và 2 role ứng dụng.
-- =====================================================================
SET DEFINE OFF
SET SERVEROUTPUT ON

-- Kiểm tra đang đứng đúng PDB (kết quả phải là FREEPDB1, KHÔNG phải CDB$ROOT)
SHOW CON_NAME

-- ---------------------------------------------------------------------
-- 1. Tạo tablespace TS_SUCO: 1 datafile 100M, tự mở rộng 10M/lần, tối đa 500M.
--    Thư mục datafile được lấy từ datafile của tablespace SYSTEM trong PDB
--    để chạy được trên cả Windows lẫn Linux mà không phải sửa đường dẫn.
-- ---------------------------------------------------------------------
DECLARE
  v_dir VARCHAR2(500);
BEGIN
  SELECT REGEXP_SUBSTR(file_name, '^.*[/\\]')
    INTO v_dir
    FROM dba_data_files
   WHERE tablespace_name = 'SYSTEM'
     AND ROWNUM = 1;

  EXECUTE IMMEDIATE
    'CREATE TABLESPACE TS_SUCO DATAFILE ''' || v_dir || 'ts_suco01.dbf'' ' ||
    'SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE 500M';
  DBMS_OUTPUT.PUT_LINE('Đã tạo TS_SUCO tại: ' || v_dir || 'ts_suco01.dbf');
END;
/

-- ---------------------------------------------------------------------
-- 2. Tạo user SUCO_APP (chủ schema), dùng TS_SUCO làm tablespace mặc định.
--    Mật khẩu demo: SuCo_App_123 (phải trùng với application.properties).
-- ---------------------------------------------------------------------
CREATE USER SUCO_APP IDENTIFIED BY "SuCo_App_123"
  DEFAULT TABLESPACE TS_SUCO
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON TS_SUCO;

-- ---------------------------------------------------------------------
-- 3. Cấp quyền hệ thống tối thiểu để SUCO_APP tạo đối tượng của mình.
-- ---------------------------------------------------------------------
GRANT CREATE SESSION   TO SUCO_APP;
GRANT CREATE TABLE     TO SUCO_APP;
GRANT CREATE SEQUENCE  TO SUCO_APP;
GRANT CREATE TRIGGER   TO SUCO_APP;
GRANT CREATE VIEW      TO SUCO_APP;
GRANT CREATE PROCEDURE TO SUCO_APP;

-- ---------------------------------------------------------------------
-- 4. Tạo 2 role ứng dụng. Quyền trên bảng sẽ được cấp ở file 06.
-- ---------------------------------------------------------------------
CREATE ROLE ROLE_APP_USER;
CREATE ROLE ROLE_APP_ADMIN;

-- Kiểm tra kết quả
SELECT username, default_tablespace FROM dba_users WHERE username = 'SUCO_APP';
SELECT tablespace_name, status FROM dba_tablespaces WHERE tablespace_name = 'TS_SUCO';
