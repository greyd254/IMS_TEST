-- =====================================================================
-- 00_run_all.sql
-- CÁCH CHẠY: mở SQL*Plus / SQLcl TRONG THƯ MỤC sql/ rồi gõ:  @00_run_all.sql
--   (sẽ hỏi mật khẩu SYSTEM một lần).
-- Gọi lần lượt: 01 (SYSTEM) -> 02, 03, 04, 05 (SUCO_APP) -> 06 (SYSTEM).
-- Xóa sạch làm lại: @99_drop_all.sql
-- =====================================================================
SET DEFINE ON
SET ECHO ON
ACCEPT sys_pw CHAR PROMPT 'Nhập mật khẩu SYSTEM: ' HIDE

-- Bước 1: tablespace + user + role (SYSTEM trên PDB)
CONNECT system/&sys_pw@localhost:1521/FREEPDB1
@01_tablespace_user.sql

-- Bước 2-5: bảng, trigger, dữ liệu, view + truy vấn (SUCO_APP)
CONNECT SUCO_APP/SuCo_App_123@localhost:1521/FREEPDB1
@02_tables.sql
@03_triggers.sql
@04_data.sql
@05_queries.sql

-- Bước 6: phân quyền + user demo (quay lại SYSTEM vì cần GRANT trên bảng của SUCO_APP)
-- Các file 02-05 đã SET DEFINE OFF nên phải bật lại để &sys_pw được thay thế
SET DEFINE ON
CONNECT system/&sys_pw@localhost:1521/FREEPDB1
@06_users_roles.sql

-- Bước 7: profile, quản trị user, role nhiều cấp (SYSTEM). Tạo user thử U_DEMO rồi tự xóa.
@08_quan_tri_user.sql
