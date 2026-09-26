-- =====================================================================
-- 06_users_roles.sql
-- CHẠY BẰNG: SYSTEM, kết nối vào FREEPDB1 (sau khi đã chạy 01 -> 05)
-- MỤC ĐÍCH : cấp quyền bảng cho 2 role, tạo 2 user demo, kiểm tra và REVOKE mẫu.
-- =====================================================================
SET DEFINE OFF
SET LINESIZE 200
SHOW CON_NAME

-- ---------------------------------------------------------------------
-- 1. ROLE_APP_USER: chỉ đọc bảng danh mục / bài kinh nghiệm; đọc và thêm SU_CO
-- ---------------------------------------------------------------------
GRANT SELECT ON SUCO_APP.DANH_MUC_SU_CO   TO ROLE_APP_USER;
GRANT SELECT ON SUCO_APP.BAI_KINH_NGHIEM  TO ROLE_APP_USER;
GRANT SELECT ON SUCO_APP.PHONG            TO ROLE_APP_USER;
GRANT SELECT ON SUCO_APP.THIET_BI         TO ROLE_APP_USER;
GRANT SELECT ON SUCO_APP.V_SU_CO_CHI_TIET TO ROLE_APP_USER;
GRANT SELECT, INSERT ON SUCO_APP.SU_CO    TO ROLE_APP_USER;

-- ---------------------------------------------------------------------
-- 2. ROLE_APP_ADMIN: toàn quyền SELECT/INSERT/UPDATE/DELETE trên mọi bảng
-- ---------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.NGUOI_DUNG      TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.PHONG           TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.THIET_BI        TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.DANH_MUC_SU_CO  TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.BAI_KINH_NGHIEM TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.SU_CO           TO ROLE_APP_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUCO_APP.LICH_SU_XU_LY   TO ROLE_APP_ADMIN;
GRANT SELECT ON SUCO_APP.V_SU_CO_CHI_TIET TO ROLE_APP_ADMIN;

-- ---------------------------------------------------------------------
-- 3. Tạo 2 user demo và gán role tương ứng (user cần CREATE SESSION để đăng nhập)
-- ---------------------------------------------------------------------
CREATE USER U_NHANVIEN IDENTIFIED BY "Nhanvien_123" DEFAULT TABLESPACE USERS;
CREATE USER U_QUANTRI  IDENTIFIED BY "Quantri_123"  DEFAULT TABLESPACE USERS;
GRANT CREATE SESSION TO U_NHANVIEN;
GRANT CREATE SESSION TO U_QUANTRI;
GRANT ROLE_APP_USER  TO U_NHANVIEN;
GRANT ROLE_APP_ADMIN TO U_QUANTRI;

-- ---------------------------------------------------------------------
-- 4. Kiểm tra quyền qua từ điển dữ liệu
-- ---------------------------------------------------------------------
-- Role nào đang được gán cho ai
SELECT grantee, granted_role FROM dba_role_privs
 WHERE grantee IN ('U_NHANVIEN', 'U_QUANTRI') ORDER BY grantee;

-- Quyền trên bảng của từng role
SELECT grantee, table_name, privilege FROM dba_tab_privs
 WHERE owner = 'SUCO_APP' AND grantee IN ('ROLE_APP_USER', 'ROLE_APP_ADMIN')
 ORDER BY grantee, table_name, privilege;

-- ---------------------------------------------------------------------
-- 5. Kiểm thử thủ công (mở phiên mới):
--    CONNECT U_NHANVIEN/Nhanvien_123@localhost:1521/FREEPDB1
--      SELECT COUNT(*) FROM SUCO_APP.SU_CO;                      -- OK
--      UPDATE SUCO_APP.SU_CO SET TRANG_THAI='MOI' WHERE 1=0;     -- ORA-01031: insufficient privileges
--      SELECT COUNT(*) FROM SUCO_APP.NGUOI_DUNG;                 -- ORA-00942 (không có quyền)
--    CONNECT U_QUANTRI/Quantri_123@localhost:1521/FREEPDB1
--      SELECT COUNT(*) FROM SUCO_APP.NGUOI_DUNG;                 -- OK
-- ---------------------------------------------------------------------

-- ---------------------------------------------------------------------
-- 6. REVOKE mẫu (chạy lại từng câu GRANT phía trên nếu muốn cấp lại)
-- ---------------------------------------------------------------------
-- Thu hồi quyền INSERT trên SU_CO khỏi role người dùng:
REVOKE INSERT ON SUCO_APP.SU_CO FROM ROLE_APP_USER;
-- Cấp lại:
GRANT INSERT ON SUCO_APP.SU_CO TO ROLE_APP_USER;
-- Thu hồi cả role khỏi một user:
-- REVOKE ROLE_APP_USER FROM U_NHANVIEN;
