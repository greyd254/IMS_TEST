#!/bin/bash
set -e
cd /sql

PW_SYS="system/${ORACLE_PASSWORD}@localhost:1521/FREEPDB1"
PW_APP="SUCO_APP/SuCo_App_123@localhost:1521/FREEPDB1"

run() {
  echo ">>> $1"
  sqlplus -S "$2" @"$1" < /dev/null
}

# STEP 1: SYSTEM - tablespace TS_SUCO + user SUCO_APP + role
run 01_tablespace_user.sql "$PW_SYS"

# STEP 2-5 + 07, 09-12: SUCO_APP - bang, trigger, du lieu, view...
run 02_tables.sql        "$PW_APP"
run 03_triggers.sql      "$PW_APP"
run 04_data.sql          "$PW_APP"
run 07_admin_demo.sql    "$PW_APP"
run 09_bam_mat_khau.sql  "$PW_APP"
run 10_them_anh_dinh_kem.sql "$PW_APP"
run 11_mo_rong_du_lieu.sql   "$PW_APP"
run 12_binh_luan.sql     "$PW_APP"

# STEP 6+8: SYSTEM - phan quyen tren bang SUCO_APP, quan tri user
run 06_users_roles.sql   "$PW_SYS"
run 08_quan_tri_user.sql "$PW_SYS"

echo ">>> Khoi tao CSDL hoan thanh."
