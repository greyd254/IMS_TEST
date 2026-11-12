-- Chay toan bo Scripts theo thu tu giong 00_run_all.sql, khong cam nhap mat khau
SET DEFINE OFF
SET ECHO ON
WHENEVER SQLERROR EXIT FAILURE

CONNECT system/Oracle123456@localhost:1521/FREEPDB1
@01_tablespace_user.sql

CONNECT SUCO_APP/SuCo_App_123@localhost:1521/FREEPDB1
@02_tables.sql
@03_triggers.sql
@04_data.sql
@07_admin_demo.sql
@09_bam_mat_khau.sql
@10_them_anh_dinh_kem.sql
@11_mo_rong_du_lieu.sql
@12_binh_luan.sql

CONNECT system/Oracle123456@localhost:1521/FREEPDB1
@06_users_roles.sql
@08_quan_tri_user.sql

EXIT SUCCESS
