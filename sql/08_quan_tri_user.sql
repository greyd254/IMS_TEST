-- =====================================================================
-- 08_quan_tri_user.sql - QUẢN TRỊ NGƯỜI DÙNG, PROFILE, PHÂN QUYỀN, ROLE
-- CHẠY BẰNG: SYSTEM, kết nối vào FREEPDB1 (sau khi đã chạy 01 -> 06).
--            sqlplus system@localhost:1521/FREEPDB1  rồi  @08_quan_tri_user.sql
-- Script hỏi mật khẩu SYSTEM một lần (biến sys_pw; hoặc đặt trước: DEFINE sys_pw=...). Cần vì bước thử
-- khóa tài khoản phải đăng nhập lại SYSTEM sau các lần CONNECT thử.
-- LƯU Ý: không viết chú thích ngay sau dấu ; trên cùng một dòng, SQL*Plus sẽ không nhận ra hết lệnh.
-- Tạo user tạm U_DEMO để thử, cuối script tự xóa. KHÔNG đụng tới SUCO_APP.
-- =====================================================================
SET DEFINE ON
SET LINESIZE 220 PAGESIZE 60 ECHO ON FEEDBACK ON
COLUMN username FORMAT A14
COLUMN profile FORMAT A18
COLUMN account_status FORMAT A18
COLUMN resource_name FORMAT A26
COLUMN limit FORMAT A12
COLUMN grantee FORMAT A18
COLUMN granted_role FORMAT A18
COLUMN privilege FORMAT A22
COLUMN table_name FORMAT A16
COLUMN role FORMAT A18
COLUMN tablespace_name FORMAT A14
SHOW CON_NAME

-- ---------------------------------------------------------------------
-- 1. PROFILE: gói giới hạn về mật khẩu và tài nguyên gán cho user.
--    Giới hạn phiên (SESSIONS_PER_USER, IDLE_TIME, CONNECT_TIME) chỉ có hiệu lực khi
--    tham số RESOURCE_LIMIT = TRUE (mặc định TRUE từ 12c).
-- ---------------------------------------------------------------------
SHOW PARAMETER resource_limit

-- Profile cho người dùng thường: chặt hơn
CREATE PROFILE PROFILE_NHANVIEN LIMIT
  FAILED_LOGIN_ATTEMPTS 3        -- nhập sai 3 lần thì khóa tài khoản
  PASSWORD_LOCK_TIME    1/24     -- tự mở khóa sau 1 giờ (đơn vị: ngày)
  PASSWORD_LIFE_TIME    90       -- mật khẩu hết hạn sau 90 ngày
  PASSWORD_GRACE_TIME   7        -- được 7 ngày cảnh báo trước khi hết hạn hẳn
  PASSWORD_REUSE_MAX    5        -- không dùng lại 5 mật khẩu gần nhất
  SESSIONS_PER_USER     2        -- tối đa 2 phiên đồng thời
  IDLE_TIME             30       -- ngắt phiên không hoạt động sau 30 phút (CONNECT_TIME: tối đa 480 phút/phiên)
  CONNECT_TIME          480;

-- Profile cho quản trị: nới hơn
CREATE PROFILE PROFILE_QUANTRI LIMIT
  FAILED_LOGIN_ATTEMPTS 5
  PASSWORD_LOCK_TIME    1/24
  PASSWORD_LIFE_TIME    60       -- quản trị phải đổi mật khẩu thường xuyên hơn
  SESSIONS_PER_USER     5
  IDLE_TIME             60;

-- Xem nội dung 2 profile vừa tạo
SELECT profile, resource_name, limit FROM dba_profiles
 WHERE profile IN ('PROFILE_NHANVIEN', 'PROFILE_QUANTRI')
   AND resource_name IN ('FAILED_LOGIN_ATTEMPTS','PASSWORD_LOCK_TIME','PASSWORD_LIFE_TIME',
                         'PASSWORD_GRACE_TIME','PASSWORD_REUSE_MAX','SESSIONS_PER_USER','IDLE_TIME','CONNECT_TIME')
 ORDER BY profile, resource_name;

-- Gán profile cho 2 user demo của file 06
ALTER USER U_NHANVIEN PROFILE PROFILE_NHANVIEN;
ALTER USER U_QUANTRI  PROFILE PROFILE_QUANTRI;

-- ---------------------------------------------------------------------
-- 2. TẠO USER: mật khẩu, tablespace mặc định/tạm, quota, profile
-- ---------------------------------------------------------------------
CREATE USER U_DEMO IDENTIFIED BY "Demo_123"
  DEFAULT TABLESPACE USERS
  TEMPORARY TABLESPACE TEMP
  QUOTA 5M ON USERS
  PROFILE PROFILE_NHANVIEN;

GRANT CREATE SESSION TO U_DEMO;

SELECT username, account_status, profile, default_tablespace, temporary_tablespace
  FROM dba_users WHERE username = 'U_DEMO';

-- ---------------------------------------------------------------------
-- 3. SỬA USER
-- ---------------------------------------------------------------------
-- 3.1 Đổi mật khẩu
ALTER USER U_DEMO IDENTIFIED BY "Demo_456";

-- 3.2 Đổi quota (xem thay đổi ở dba_ts_quotas; MAX_BYTES = -1 nghĩa là UNLIMITED)
ALTER USER U_DEMO QUOTA 20M ON USERS;
SELECT username, tablespace_name, max_bytes/1024/1024 AS QUOTA_MB FROM dba_ts_quotas WHERE username = 'U_DEMO';

-- 3.3 Đổi profile sang PROFILE_QUANTRI rồi trả lại
ALTER USER U_DEMO PROFILE PROFILE_QUANTRI;
SELECT username, profile FROM dba_users WHERE username = 'U_DEMO';
ALTER USER U_DEMO PROFILE PROFILE_NHANVIEN;

-- 3.4 Buộc đổi mật khẩu ở lần đăng nhập sau (trạng thái EXPIRED), rồi đặt lại mật khẩu để mở
ALTER USER U_DEMO PASSWORD EXPIRE;
SELECT username, account_status, expiry_date FROM dba_users WHERE username = 'U_DEMO';
ALTER USER U_DEMO IDENTIFIED BY "Demo_789";
SELECT username, account_status FROM dba_users WHERE username = 'U_DEMO';

-- 3.5 Khóa / mở khóa tài khoản thủ công
ALTER USER U_DEMO ACCOUNT LOCK;
SELECT username, account_status, lock_date FROM dba_users WHERE username = 'U_DEMO';
ALTER USER U_DEMO ACCOUNT UNLOCK;
SELECT username, account_status FROM dba_users WHERE username = 'U_DEMO';

-- ---------------------------------------------------------------------
-- 4. THỬ CƠ CHẾ KHÓA TỰ ĐỘNG (FAILED_LOGIN_ATTEMPTS = 3)
--    Đăng nhập sai mật khẩu 3 lần liên tiếp -> tài khoản bị khóa tự động.
-- ---------------------------------------------------------------------
CONNECT U_DEMO/sai_mat_khau_1@localhost:1521/FREEPDB1
CONNECT U_DEMO/sai_mat_khau_2@localhost:1521/FREEPDB1
CONNECT U_DEMO/sai_mat_khau_3@localhost:1521/FREEPDB1
-- Lần thứ 4: dù nhập ĐÚNG mật khẩu vẫn bị từ chối vì đã bị khóa (ORA-28000)
CONNECT U_DEMO/Demo_789@localhost:1521/FREEPDB1

CONNECT system/&&sys_pw@localhost:1521/FREEPDB1
SELECT username, account_status, lock_date FROM dba_users WHERE username = 'U_DEMO';
-- Admin mở khóa thủ công (không cần đợi PASSWORD_LOCK_TIME)
ALTER USER U_DEMO ACCOUNT UNLOCK;
SELECT username, account_status FROM dba_users WHERE username = 'U_DEMO';
-- Đăng nhập lại đúng mật khẩu: thành công
CONNECT U_DEMO/Demo_789@localhost:1521/FREEPDB1
SELECT USER AS DANG_NHAP_BANG, SYS_CONTEXT('USERENV', 'CON_NAME') AS CSDL FROM dual;
CONNECT system/&&sys_pw@localhost:1521/FREEPDB1

-- ---------------------------------------------------------------------
-- 5. QUYỀN HỆ THỐNG, QUYỀN ĐỐI TƯỢNG VÀ ROLE (CHỨC DANH) NHIỀU CẤP
-- ---------------------------------------------------------------------
-- 5.1 Quyền hệ thống trực tiếp cho user
GRANT CREATE TABLE TO U_DEMO;

-- 5.2 Quyền đối tượng: đọc bảng PHONG của SUCO_APP, kèm quyền cấp tiếp cho người khác
GRANT SELECT ON SUCO_APP.PHONG TO U_DEMO WITH GRANT OPTION;

-- 5.3 Role nhiều cấp: ROLE_TRUONG_NHOM = quyền của ROLE_APP_USER (file 06) + CREATE VIEW
CREATE ROLE ROLE_TRUONG_NHOM;
GRANT ROLE_APP_USER TO ROLE_TRUONG_NHOM;
GRANT CREATE VIEW   TO ROLE_TRUONG_NHOM;
GRANT ROLE_TRUONG_NHOM TO U_DEMO;

-- 5.4 Truy vấn quyền qua từ điển dữ liệu
SELECT grantee, granted_role FROM dba_role_privs WHERE grantee IN ('U_DEMO', 'ROLE_TRUONG_NHOM') ORDER BY 1, 2;
SELECT grantee, privilege FROM dba_sys_privs WHERE grantee IN ('U_DEMO', 'ROLE_TRUONG_NHOM') ORDER BY 1, 2;
SELECT grantee, table_name, privilege, grantable FROM dba_tab_privs WHERE grantee = 'U_DEMO';

-- 5.5 Kiểm chứng quyền hiệu lực khi U_DEMO đăng nhập (SESSION_PRIVS gồm cả quyền lấy từ role)
CONNECT U_DEMO/Demo_789@localhost:1521/FREEPDB1
SELECT * FROM session_roles;
SELECT privilege FROM session_privs ORDER BY 1;
SELECT COUNT(*) AS SO_PHONG_DOC_DUOC FROM SUCO_APP.PHONG;
SELECT COUNT(*) AS SO_SU_CO_DOC_DUOC FROM SUCO_APP.SU_CO;
-- Không có quyền UPDATE bảng SU_CO -> phải lỗi (Oracle 23ai báo ORA-41900, bản cũ báo ORA-01031)
UPDATE SUCO_APP.SU_CO SET MO_TA = MO_TA WHERE 1 = 0;
-- Có quyền CREATE TABLE và quota trên USERS -> tạo được bảng của chính mình
CREATE TABLE BANG_THU (ID NUMBER);
DROP TABLE BANG_THU PURGE;
CONNECT system/&&sys_pw@localhost:1521/FREEPDB1

-- 5.6 Thu hồi quyền / role
REVOKE SELECT ON SUCO_APP.PHONG FROM U_DEMO;
REVOKE CREATE TABLE FROM U_DEMO;
REVOKE ROLE_TRUONG_NHOM FROM U_DEMO;
SELECT grantee, granted_role FROM dba_role_privs WHERE grantee = 'U_DEMO';
SELECT grantee, privilege FROM dba_sys_privs WHERE grantee = 'U_DEMO';

-- ---------------------------------------------------------------------
-- 6. THEO DÕI PHIÊN ĐANG KẾT NỐI
-- ---------------------------------------------------------------------
SELECT username, COUNT(*) AS SO_PHIEN FROM v$session
 WHERE username IS NOT NULL GROUP BY username ORDER BY username;
-- Kết thúc 1 phiên cụ thể (thay SID, SERIAL# lấy từ V$SESSION):
-- ALTER SYSTEM KILL SESSION 'sid,serial#' IMMEDIATE;

-- ---------------------------------------------------------------------
-- 7. XÓA USER VÀ ROLE THỬ
--    CASCADE xóa luôn mọi đối tượng thuộc user. Chỉ xóa được khi user không còn phiên đăng nhập.
--    2 profile PROFILE_NHANVIEN / PROFILE_QUANTRI và việc gán cho U_NHANVIEN / U_QUANTRI được GIỮ LẠI.
-- ---------------------------------------------------------------------
DROP USER U_DEMO CASCADE;
DROP ROLE ROLE_TRUONG_NHOM;
SELECT COUNT(*) AS U_DEMO_CON_LAI FROM dba_users WHERE username = 'U_DEMO';
SELECT username, profile FROM dba_users WHERE username IN ('U_NHANVIEN', 'U_QUANTRI') ORDER BY 1;
