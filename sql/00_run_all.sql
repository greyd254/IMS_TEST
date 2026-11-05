-- =====================================================================
-- 00_run_all.sql
-- CÁCH CHẠY: mở SQL*Plus / SQLcl TRONG THƯ MỤC sql/ rồi gõ:  @00_run_all.sql
--   (sẽ hỏi mật khẩu SYSTEM một lần).
-- Gọi lần lượt: 01 (SYSTEM) -> 02, 03, 04, 05 (SUCO_APP).
-- Xóa sạch làm lại: @99_drop_all.sql
-- =====================================================================
SET DEFINE ON
SET ECHO ON
ACCEPT sys_pw CHAR PROMPT 'Nhập mật khẩu SYSTEM: ' HIDE

-- Bước 1: tablespace + user ứng dụng (SYSTEM trên PDB)
CONNECT system/&sys_pw@localhost:1521/FREEPDB1
@01_tablespace_user.sql

-- Bước 2-5: bảng, trigger, dữ liệu mẫu, view (SUCO_APP)
CONNECT SUCO_APP/SuCo_App_123@localhost:1521/FREEPDB1
@02_tables.sql
@03_triggers.sql
@04_data.sql
@05_views.sql
