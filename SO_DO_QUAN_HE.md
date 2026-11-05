# Sơ đồ quan hệ CSDL - Hệ thống quản lý sự cố CNTT

Schema `SUCO_APP`, tablespace `TS_SUCO`, 9 bảng. Có thể dán khối Mermaid dưới đây vào bất kỳ công cụ nào hỗ
trợ Mermaid (GitHub, VS Code, mermaid.live) để xem hình.

```mermaid
erDiagram
    NGUOI_DUNG ||--o{ SU_CO : "báo (MA_NGUOI_BAO)"
    NGUOI_DUNG |o--o{ SU_CO : "xử lý (MA_ADMIN_XU_LY)"
    NGUOI_DUNG ||--o{ BAI_KINH_NGHIEM : "viết (MA_NGUOI_VIET)"
    NGUOI_DUNG |o--o{ LICH_SU_XU_LY : "cập nhật (MA_NGUOI_CAP_NHAT)"
    NGUOI_DUNG |o--o{ NGUOI_DUNG : "tạo tài khoản (MA_NGUOI_TAO)"
    NGUOI_DUNG |o--o{ NGUOI_DUNG : "vô hiệu hóa (MA_NGUOI_XOA)"
    NGUOI_DUNG ||--o{ PHAN_HOI : "gửi phản hồi (MA_NGUOI_PHAN_HOI)"
    NGUOI_DUNG ||--o{ BINH_LUAN_SU_CO : "gửi tin (MA_NGUOI_GUI)"
    PHONG ||--o{ THIET_BI : "chứa"
    THIET_BI |o--o{ SU_CO : "gặp sự cố (MA_TB)"
    DANH_MUC_SU_CO ||--o{ SU_CO : "phân loại"
    DANH_MUC_SU_CO ||--o{ BAI_KINH_NGHIEM : "phân loại"
    BAI_KINH_NGHIEM |o--o{ SU_CO : "tham chiếu (MA_BAI_KN)"
    SU_CO ||--o{ LICH_SU_XU_LY : "có lịch sử"
    SU_CO ||--o| PHAN_HOI : "được phản hồi (MA_SC)"
    SU_CO ||--o{ BINH_LUAN_SU_CO : "trao đổi (MA_SC)"

    NGUOI_DUNG {
        NUMBER MA_ND PK
        VARCHAR2 HO_TEN
        VARCHAR2 EMAIL UK
        VARCHAR2 MAT_KHAU
        VARCHAR2 DON_VI
        VARCHAR2 VAI_TRO "USER | ADMIN"
        VARCHAR2 SDT
        VARCHAR2 TRANG_THAI "HOAT_DONG | KHONG_HOAT_DONG"
        DATE NGAY_TAO
        NUMBER MA_NGUOI_TAO FK
        DATE NGAY_XOA
        NUMBER MA_NGUOI_XOA FK
    }
    PHONG {
        NUMBER MA_PHONG PK
        VARCHAR2 TEN_PHONG "UK (cùng TOA_NHA, TANG)"
        VARCHAR2 TOA_NHA
        NUMBER TANG "cho phép NULL"
        VARCHAR2 LOAI_PHONG "PHONG_MAY | PHONG_HOC | VAN_PHONG"
    }
    THIET_BI {
        NUMBER MA_TB PK
        VARCHAR2 TEN_TB "UK (cùng MA_PHONG, LOAI_TB)"
        VARCHAR2 LOAI_TB
        NUMBER MA_PHONG FK
        VARCHAR2 TINH_TRANG "HOAT_DONG | HONG | DANG_SUA"
        VARCHAR2 NHA_CUNG_CAP
        DATE NGAY_LAP_DAT
        VARCHAR2 HDSD
    }
    PHAN_HOI {
        NUMBER MA_PH PK
        NUMBER MA_SC FK "UK"
        NUMBER MA_NGUOI_PHAN_HOI FK
        NUMBER MUC_HAI_LONG "1 - 5"
        VARCHAR2 NOI_DUNG
        DATE NGAY_PHAN_HOI
    }
    BINH_LUAN_SU_CO {
        NUMBER MA_BL PK
        NUMBER MA_SC FK
        NUMBER MA_NGUOI_GUI FK
        VARCHAR2 NOI_DUNG
        VARCHAR2 TEP_DINH_KEM
        TIMESTAMP THOI_GIAN
    }
    DANH_MUC_SU_CO {
        NUMBER MA_DM PK
        VARCHAR2 TEN_DM UK
        VARCHAR2 MO_TA
    }
    BAI_KINH_NGHIEM {
        NUMBER MA_BAI PK
        VARCHAR2 TIEU_DE
        NUMBER MA_DM FK
        VARCHAR2 TRIEU_CHUNG
        VARCHAR2 NGUYEN_NHAN
        VARCHAR2 GIAI_PHAP
        NUMBER MA_NGUOI_VIET FK
        DATE NGAY_TAO
    }
    SU_CO {
        NUMBER MA_SC PK
        VARCHAR2 TIEU_DE
        VARCHAR2 MO_TA
        NUMBER MA_NGUOI_BAO FK
        NUMBER MA_ADMIN_XU_LY FK
        NUMBER MA_TB FK
        NUMBER MA_DM FK
        VARCHAR2 MUC_UU_TIEN "THAP | TRUNG_BINH | CAO"
        VARCHAR2 TRANG_THAI "MOI | DANG_XU_LY | HOAN_THANH"
        VARCHAR2 NGUYEN_NHAN
        VARCHAR2 CACH_KHAC_PHUC
        NUMBER MA_BAI_KN FK
        VARCHAR2 ANH_DINH_KEM
        DATE NGAY_TAO
        DATE NGAY_HOAN_THANH
    }
    LICH_SU_XU_LY {
        NUMBER MA_LS PK
        NUMBER MA_SC FK
        NUMBER MA_NGUOI_CAP_NHAT FK
        VARCHAR2 TRANG_THAI_CU
        VARCHAR2 TRANG_THAI_MOI
        VARCHAR2 GHI_CHU
        TIMESTAMP THOI_GIAN
    }
```

## Mô tả các bảng

| Bảng | Vai trò | Khóa ngoại |
|---|---|---|
| NGUOI_DUNG | Sinh viên, giảng viên, cán bộ và admin (bộ phận IT) | MA_NGUOI_TAO, MA_NGUOI_XOA -> NGUOI_DUNG (tự tham chiếu) |
| PHONG | Phòng máy, phòng học, văn phòng theo tòa nhà và tầng | - |
| THIET_BI | Thiết bị đặt trong phòng | MA_PHONG -> PHONG |
| DANH_MUC_SU_CO | Danh mục dùng chung cho sự cố và bài kinh nghiệm | - |
| BAI_KINH_NGHIEM | Kho kinh nghiệm xử lý theo danh mục | MA_DM -> DANH_MUC_SU_CO; MA_NGUOI_VIET -> NGUOI_DUNG |
| SU_CO | Bảng trung tâm: sự cố do người dùng báo, admin xử lý | MA_NGUOI_BAO, MA_ADMIN_XU_LY -> NGUOI_DUNG; MA_TB -> THIET_BI; MA_DM -> DANH_MUC_SU_CO; MA_BAI_KN -> BAI_KINH_NGHIEM |
| LICH_SU_XU_LY | Nhật ký đổi trạng thái, do trigger ghi | MA_SC -> SU_CO; MA_NGUOI_CAP_NHAT -> NGUOI_DUNG |
| PHAN_HOI | Phản hồi 1-5 sao của người báo sau khi sự cố HOAN_THANH | MA_SC -> SU_CO (UNIQUE: mỗi sự cố 1 phản hồi); MA_NGUOI_PHAN_HOI -> NGUOI_DUNG |
| BINH_LUAN_SU_CO | Trao đổi qua lại (không giới hạn số lần) giữa người báo và admin trên một sự cố | MA_SC -> SU_CO; MA_NGUOI_GUI -> NGUOI_DUNG |

## Ràng buộc và đối tượng đi kèm

- **Cột cho phép NULL trong SU_CO:** `MA_ADMIN_XU_LY` (chưa có admin nhận), `MA_TB` (sự cố không gắn thiết bị, ví dụ quên mật khẩu), `MA_BAI_KN`, `NGUYEN_NHAN`, `CACH_KHAC_PHUC`, `ANH_DINH_KEM`, `NGAY_HOAN_THANH`.
- **Xóa mềm NGUOI_DUNG:** không có DELETE, web chỉ chuyển `TRANG_THAI` sang `KHONG_HOAT_DONG` và ghi `NGAY_XOA`/`MA_NGUOI_XOA`; tài khoản không hoạt động không đăng nhập được, dữ liệu cũ (sự cố, bài kinh nghiệm, lịch sử) vẫn giữ nguyên.
- **PHONG.TANG cho phép NULL:** một số văn phòng chỉ có 1 tầng nên không cần ghi số tầng.
- **Chống trùng tên ở tầng CSDL:** `UQ_PHONG_TEN` (cùng tòa, tầng, tên) và `UQ_THIET_BI_TEN` (cùng phòng, loại, tên) là function-based unique index, không phân biệt hoa thường; web cũng tự kiểm tra trùng trước khi lưu, index chỉ là lớp bảo vệ cuối.
- **CHECK:** `CK_SU_CO_NGAY_HT` bảo đảm `NGAY_HOAN_THANH >= NGAY_TAO`; `CK_PHAN_HOI_MUC` giới hạn `MUC_HAI_LONG` từ 1-5; các CHECK khác giới hạn giá trị vai trò, trạng thái người dùng, loại phòng, loại và tình trạng thiết bị, mức ưu tiên, trạng thái sự cố.
- **Trigger:** `TRG_SU_CO_LICH_SU` ghi `LICH_SU_XU_LY` khi `TRANG_THAI` đổi; `TRG_SU_CO_HOAN_THANH` tự gán `NGAY_HOAN_THANH`.
- **View:** `V_SU_CO_CHI_TIET` ghép SU_CO với người báo, admin, thiết bị, phòng, danh mục; `V_SU_CO_THUONG_GAP` đếm số sự cố theo danh mục (phục vụ mục "sự cố hay gặp" ở trang người dùng) — tính bằng truy vấn thay vì lưu cột tần suất tĩnh để khỏi lệch dữ liệu; web đọc từ 2 view này (`sql/05_views.sql`).
