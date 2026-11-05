-- =====================================================================
-- 99_drop_all.sql
-- CÁCH CHẠY: trong thư mục sql/, gõ  @99_drop_all.sql  (hỏi mật khẩu SYSTEM).
-- MỤC ĐÍCH : xóa sạch user SUCO_APP cùng toàn bộ bảng/trigger/view của nó,
--            và tablespace TS_SUCO, để chạy lại từ đầu.
-- CẢNH BÁO : mất toàn bộ dữ liệu. Tắt web trước khi chạy.
-- =====================================================================
SET DEFINE ON
ACCEPT sys_pw CHAR PROMPT 'Nhập mật khẩu SYSTEM: ' HIDE
CONNECT system/&sys_pw@localhost:1521/FREEPDB1

-- Nếu một lệnh báo "does not exist" thì bỏ qua, chạy tiếp các lệnh sau
WHENEVER SQLERROR CONTINUE

-- CASCADE xóa luôn mọi đối tượng thuộc SUCO_APP (bảng, trigger, view, index)
DROP USER SUCO_APP CASCADE;

-- Xóa tablespace kèm datafile trên đĩa
DROP TABLESPACE TS_SUCO INCLUDING CONTENTS AND DATAFILES;

SELECT COUNT(*) AS CON_LAI_USER FROM dba_users WHERE username = 'SUCO_APP';
SELECT COUNT(*) AS CON_LAI_TS FROM dba_tablespaces WHERE tablespace_name = 'TS_SUCO';
