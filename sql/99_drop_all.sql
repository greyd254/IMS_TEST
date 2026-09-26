-- =====================================================================
-- 99_drop_all.sql
-- CÁCH CHẠY: trong thư mục sql/, gõ  @99_drop_all.sql  (hỏi mật khẩu SYSTEM).
-- MỤC ĐÍCH : xóa sạch mọi thứ đã tạo (user demo, role, user SUCO_APP cùng toàn bộ
--            bảng/trigger/view của nó, và tablespace TS_SUCO) để chạy lại từ đầu.
-- CẢNH BÁO : mất toàn bộ dữ liệu. Ngắt các phiên đang dùng SUCO_APP (tắt web) trước khi chạy.
-- =====================================================================
SET DEFINE ON
ACCEPT sys_pw CHAR PROMPT 'Nhập mật khẩu SYSTEM: ' HIDE
CONNECT system/&sys_pw@localhost:1521/FREEPDB1

-- Nếu một lệnh báo "does not exist" thì bỏ qua, chạy tiếp các lệnh sau
WHENEVER SQLERROR CONTINUE

DROP USER U_NHANVIEN CASCADE;
DROP USER U_QUANTRI CASCADE;
DROP USER U_DEMO CASCADE;
DROP ROLE ROLE_APP_USER;
DROP ROLE ROLE_APP_ADMIN;
DROP ROLE ROLE_TRUONG_NHOM;
-- Profile chỉ xóa được sau khi không còn user nào dùng nó (U_NHANVIEN, U_QUANTRI đã xóa ở trên)
DROP PROFILE PROFILE_NHANVIEN;
DROP PROFILE PROFILE_QUANTRI;

-- CASCADE xóa luôn mọi đối tượng thuộc SUCO_APP (bảng, trigger, view, index)
DROP USER SUCO_APP CASCADE;

-- Xóa tablespace kèm datafile trên đĩa
DROP TABLESPACE TS_SUCO INCLUDING CONTENTS AND DATAFILES;

SELECT COUNT(*) AS CON_LAI_USER FROM dba_users WHERE username IN ('SUCO_APP', 'U_NHANVIEN', 'U_QUANTRI');
SELECT COUNT(*) AS CON_LAI_TS FROM dba_tablespaces WHERE tablespace_name = 'TS_SUCO';
