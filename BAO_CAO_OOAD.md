# BÁO CÁO BÀI TẬP LỚN

## Học phần: Phân tích và thiết kế hướng đối tượng (OOAD)

## Đề tài: Hệ thống Quản lý sự cố CNTT và kho kinh nghiệm xử lý

| | |
|---|---|
| Nhóm | *(điền tên nhóm)* |
| Lớp | *(điền lớp)* |
| Thành viên | *(điền họ tên — MSV — nhiệm vụ, xem bảng phân công ở Phụ lục)* |
| Giảng viên hướng dẫn | *(điền tên GVHD)* |
| Ngày | *(điền ngày nộp)* |

---

### Ghi chú về đề tài

Đề tài "Quản lý sự cố CNTT" không nằm trong 10 đề tài gợi ý ở Mục 8 của hướng dẫn BTL, nhóm chọn theo diện
"đề xuất đề tài khác có độ phức tạp tương đương" (Mục 2 hướng dẫn). Đây **chính là bài toán nhóm đã phân
tích, thiết kế và cài đặt cho đồ án Java** (xem [README.md](README.md), [SO_DO_QUAN_HE.md](SO_DO_QUAN_HE.md)
và mã nguồn trong `src/`) — báo cáo này dùng lại đúng bài toán đó để trình bày quy trình OOAD: từ yêu cầu
nghiệp vụ → mô hình phân tích → mô hình thiết kế, đối chiếu lại với ứng dụng đã chạy được. Nhóm **cần xin
xác nhận của giảng viên** trước khi nộp chính thức.

### Ghi chú sử dụng công cụ AI (theo Mục 6 của hướng dẫn)

Bộ khung báo cáo này (cấu trúc chương, các sơ đồ UML dạng Mermaid, bảng CRC/test case) được soạn với sự hỗ
trợ của Claude (Anthropic), dựa trên việc đọc trực tiếp mã nguồn ứng dụng đã có (`src/main/java/...`) và
tài liệu `sql/`, `SO_DO_QUAN_HE.md` của chính dự án. **Đây là bản nháp có căn cứ, không phải bản hoàn
chỉnh**: nhóm bắt buộc phải tự đọc lại toàn bộ, chạy thử ứng dụng để kiểm chứng các sơ đồ tương tác, điều
chỉnh những chỗ chưa đúng ý đồ thiết kế ban đầu của nhóm, tự điền phần giả định/quyết định mang tính chủ
quan (ví dụ phần "điểm mơ hồ" ở Chương 2), và phải giải thích được mọi nội dung khi bảo vệ.

---

## Mục lục

1. [Bài toán và phân tích yêu cầu](#chương-1-bài-toán-và-phân-tích-yêu-cầu)
2. [Mô hình phân tích](#chương-2-mô-hình-phân-tích)
3. [Thiết kế hệ thống](#chương-3-thiết-kế-hệ-thống)
4. [Cài đặt và kiểm thử](#chương-4-cài-đặt-và-kiểm-thử)
5. [Kết luận](#kết-luận)
6. [Tài liệu tham khảo](#tài-liệu-tham-khảo)
7. [Phụ lục](#phụ-lục)

---

## CHƯƠNG 1. BÀI TOÁN VÀ PHÂN TÍCH YÊU CẦU

### 1.1. Giới thiệu bài toán, mục tiêu và phạm vi

Một trường đại học có nhiều phòng máy, phòng học và văn phòng, mỗi phòng có các thiết bị CNTT (máy tính,
máy chiếu, máy in, thiết bị mạng...). Sinh viên, giảng viên và cán bộ thường xuyên gặp sự cố liên quan đến
thiết bị, mạng, phần mềm hoặc tài khoản, và cần báo cho bộ phận CNTT xử lý. Hiện việc báo sự cố và theo dõi
xử lý còn thủ công (gọi điện, nhắn tin rời rạc), không có nơi tra cứu lại cách xử lý các sự cố tương tự đã
gặp trước đó.

**Mục tiêu hệ thống:**

- Cho người dùng (sinh viên/giảng viên/cán bộ) báo sự cố, theo dõi tiến độ xử lý, trao đổi với bộ phận
  CNTT và đánh giá mức hài lòng sau khi xong.
- Cho cán bộ CNTT tiếp nhận, xử lý, ghi nhận nguyên nhân/cách khắc phục, và tích lũy lại thành kho kinh
  nghiệm dùng chung.
- Cho cả hai bên tra cứu kho kinh nghiệm để tự xử lý các sự cố đã từng gặp, giảm tải cho bộ phận CNTT.
- Hỗ trợ cán bộ CNTT quản lý danh mục liên quan (phòng, thiết bị, loại sự cố, tài khoản người dùng) và xem
  thống kê tổng quan.

**Phạm vi trong hệ thống:**

- Quy trình báo cáo → tiếp nhận → xử lý → phản hồi → lưu kinh nghiệm của một sự cố CNTT.
- Trao đổi qua lại (nhắn tin kèm file) giữa người báo và cán bộ CNTT trên một sự cố.
- Quản trị danh mục phục vụ trực tiếp quy trình trên: phòng, thiết bị, danh mục sự cố, tài khoản người
  dùng, kho kinh nghiệm.
- Thống kê tình hình xử lý sự cố và xuất báo cáo Excel.

**Phạm vi chủ động không thực hiện:**

- Không quản lý mua sắm, bảo trì định kỳ hay vòng đời tài sản của thiết bị (chỉ theo dõi tình trạng
  HOẠT_ĐỘNG/HỎNG/ĐANG_SỬA tại thời điểm hiện tại).
- Không gửi thông báo qua email/SMS khi sự cố đổi trạng thái (người dùng phải tự vào hệ thống xem).
- Không phân quyền nhiều cấp hay phân ca trực của cán bộ CNTT — chỉ 2 vai trò USER/ADMIN, bất kỳ ADMIN nào
  cũng xử lý được mọi sự cố.
- Không có luồng tự đăng ký tài khoản công khai — tài khoản do ADMIN tạo sẵn (xem UC "Quản lý người dùng").

### 1.2. Giả định

| # | Giả định | Vì sao cần giả định |
|---|---|---|
| G1 | Một sự cố thuộc đúng 1 danh mục và (nếu có) gắn với đúng 1 thiết bị. | Đề bài không mô tả sự cố liên quan nhiều thiết bị cùng lúc. |
| G2 | Tại một thời điểm, một sự cố chỉ có tối đa 1 cán bộ CNTT phụ trách (không đồng xử lý). | Đơn giản hóa việc theo dõi trách nhiệm xử lý. |
| G3 | Một thiết bị thuộc đúng 1 phòng (không dùng chung nhiều phòng). | Phản ánh thực tế bố trí vật lý của thiết bị. |
| G4 | Email là định danh duy nhất của một người dùng và không đổi vai trò tự do (chỉ ADMIN đổi vai trò người khác). | Dùng làm tài khoản đăng nhập. |
| G5 | "Xóa" người dùng nghĩa là vô hiệu hóa (không đăng nhập được), không xóa hẳn dữ liệu lịch sử liên quan. | Giữ lại lịch sử sự cố/kinh nghiệm đã gắn với người đó để tra cứu. |

### 1.3. Quy tắc nghiệp vụ quan trọng

- **R1 — Vòng đời xử lý:** một sự cố có 3 trạng thái `MỚI`, `ĐANG_XỬ_LÝ`, `HOÀN_THÀNH`; theo quy ước nghiệp
  vụ thì xử lý đi theo thứ tự `MỚI → ĐANG_XỬ_LÝ → HOÀN_THÀNH` (xem thảo luận ở §3.4 và §2.4 — hệ thống hiện
  tại không ép buộc thứ tự này ở tầng ứng dụng).
- **R2 — Tự động hóa khi đổi trạng thái:** mỗi lần trạng thái thực sự thay đổi, hệ thống tự ghi lại 1 dòng
  lịch sử xử lý; khi chuyển **sang** `HOÀN_THÀNH` thì tự gán ngày hoàn thành, khi chuyển **khỏi**
  `HOÀN_THÀNH` thì tự xóa ngày hoàn thành (để số liệu thống kê thời gian xử lý không bị sai).
- **R3 — Phản hồi 1 lần:** người báo chỉ gửi được đánh giá mức hài lòng (1–5 sao) sau khi sự cố đã
  `HOÀN_THÀNH`, và mỗi sự cố chỉ nhận đúng 1 phản hồi.
- **R4 — Trao đổi không giới hạn:** người báo và cán bộ CNTT có thể nhắn qua lại nhiều lần trên một sự cố ở
  bất kỳ trạng thái nào (không cần chờ hoàn thành), mỗi tin có thể kèm 1 file.
- **R5 — Tận dụng kinh nghiệm cũ:** khi xử lý xong, cán bộ CNTT có thể gắn sự cố với 1 bài kinh nghiệm có
  sẵn, **hoặc** tự động tạo một bài kinh nghiệm mới ngay từ nguyên nhân/cách khắc phục vừa nhập (không phải
  mở màn hình soạn bài riêng).
- **R6 — Chống trùng tên:** tên phòng (trong cùng tòa nhà, cùng tầng) và tên thiết bị (trong cùng phòng,
  cùng loại) không được trùng nhau, không phân biệt hoa/thường.
- **R7 — Ràng buộc xóa:** không xóa được phòng còn thiết bị, danh mục còn sự cố/bài kinh nghiệm tham chiếu,
  hoặc bài kinh nghiệm đang được một sự cố tham chiếu — phải xử lý (chuyển/xóa) các bản ghi phụ thuộc
  trước.
- **R8 — File đính kèm:** ảnh báo sự cố chỉ nhận JPG/PNG/GIF/WEBP; file trao đổi nhận thêm PDF/Word/TXT/ZIP;
  cả hai giới hạn tối đa 5MB/file.

### 1.4. Actor và mục tiêu

| Actor | Vai trò | Mục tiêu chính |
|---|---|---|
| **Người dùng hệ thống** *(actor khái quát)* | Chung cho mọi tài khoản đã đăng nhập | Đăng nhập/đăng xuất; đây là actor cha, không tự thao tác nghiệp vụ nào khác. |
| **Người báo sự cố** *(kế thừa Người dùng hệ thống)* | Sinh viên, giảng viên, cán bộ (`VAI_TRO = USER`) | Báo sự cố gặp phải, theo dõi tiến độ xử lý, trao đổi với CNTT, đánh giá sau khi xong, tự tra cứu kinh nghiệm xử lý. |
| **Cán bộ CNTT** *(kế thừa Người dùng hệ thống)* | Nhân viên bộ phận CNTT (`VAI_TRO = ADMIN`) | Tiếp nhận và xử lý sự cố, xây dựng kho kinh nghiệm, quản trị danh mục/thiết bị/phòng/người dùng, xem thống kê. |

Hệ thống không có actor hệ thống ngoài (không tích hợp email/SMS/hệ thống khác).

### 1.5. Use Case Diagram

> *Ghi chú: Mermaid không có ký hiệu actor hình que và hình ellipse UC chuẩn UML, nên sơ đồ dưới dùng hình
> "viên thuốc" (stadium) cho cả actor lẫn use case; đường liền là association, đường nét đứt có nhãn
> `«extend»` đúng ý nghĩa UML. Để sơ đồ không bị chồng chéo, actor **Người báo sự cố** đặt ở phía trên/trái,
> **Cán bộ CNTT** ở bên phải; các UC xếp thành 3 cột: cột giữa trên là UC dùng chung của cả hai actor (UC1,
> UC4, UC8), phía dưới là UC riêng của từng actor.*

```mermaid
flowchart LR
    NguoiBao(["👤 Người báo sự cố<br/>(USER)"])

    subgraph HeThong["Hệ thống Quản lý sự cố CNTT"]
        direction TB
        subgraph RiengUser[" "]
            direction TB
            UC2([UC2. Báo sự cố])
            UC3([UC3. Theo dõi sự cố của tôi])
            UC5([UC5. Gửi phản hồi sau hoàn thành])
        end
        subgraph DungChung[" "]
            direction TB
            UC1([UC1. Đăng nhập / Đăng xuất])
            UC4([UC4. Trao đổi về sự cố])
            UC8([UC8. Tra cứu kho kinh nghiệm])
        end
        subgraph RiengAdmin[" "]
            direction TB
            UC6([UC6. Tiếp nhận và xử lý sự cố])
            UC7([UC7. Lưu kinh nghiệm xử lý])
            UC9([UC9. Quản lý kho kinh nghiệm])
            UC10([UC10. Quản lý danh mục sự cố])
            UC11([UC11. Quản lý phòng])
            UC12([UC12. Quản lý thiết bị])
            UC13([UC13. Nhập thiết bị từ Excel])
            UC14([UC14. Quản lý người dùng])
            UC15([UC15. Xem thống kê])
            UC16([UC16. Xuất Excel danh sách sự cố])
        end
    end

    Admin(["👤 Cán bộ CNTT<br/>(ADMIN)"])

    NguoiBao --- UC2
    NguoiBao --- UC3
    NguoiBao --- UC5
    NguoiBao --- UC1
    NguoiBao --- UC4
    NguoiBao --- UC8

    UC1 --- Admin
    UC4 --- Admin
    UC8 --- Admin
    UC6 --- Admin
    UC9 --- Admin
    UC10 --- Admin
    UC11 --- Admin
    UC12 --- Admin
    UC14 --- Admin
    UC15 --- Admin

    UC7 -. "«extend»" .-> UC6
    UC13 -. "«extend»" .-> UC12
    UC16 -. "«extend»" .-> UC15

    style RiengUser fill:none,stroke:none
    style DungChung fill:none,stroke:none
    style RiengAdmin fill:none,stroke:none
```

Điều kiện của các quan hệ `«extend»` (để trong ghi chú thay vì trên mũi tên cho sơ đồ gọn):

| Use case mở rộng | Use case gốc | Điều kiện mở rộng |
|---|---|---|
| UC7. Lưu kinh nghiệm xử lý | UC6. Tiếp nhận và xử lý sự cố | `luuKho = true` và chưa chọn bài kinh nghiệm có sẵn |
| UC13. Nhập thiết bị từ Excel | UC12. Quản lý thiết bị | Nhập hàng loạt thay vì thêm từng dòng |
| UC16. Xuất Excel danh sách sự cố | UC15. Xem thống kê | Xuất ra file thay vì chỉ xem trên màn hình |

Hai actor đều kế thừa từ actor trừu tượng **Người dùng hệ thống** (§1.4). Trên sơ đồ, quan hệ kế thừa này
được thể hiện gián tiếp bằng việc cả hai actor cùng nối tới UC1 (Đăng nhập / Đăng xuất), UC dùng chung duy
nhất mà actor cha sở hữu; cách này tránh thêm một actor và hai mũi tên kế thừa cắt ngang các UC khác.

Không dùng `«include»` cho "Đăng nhập" trên từng UC còn lại để tránh làm sơ đồ rối (đăng nhập là **tiền
điều kiện** của mọi UC khác, không phải một bước con lặp lại có ý nghĩa nghiệp vụ riêng — xem khuyến cáo ở
Mục 4.1 của hướng dẫn BTL).

### 1.6. Danh sách Use Case

| Mã | Tên Use Case | Actor | Mô tả ngắn |
|---|---|---|---|
| UC1 | Đăng nhập / Đăng xuất | Người dùng hệ thống | Xác thực bằng email + mật khẩu, điều hướng theo vai trò. |
| UC2 | Báo sự cố | Người báo | Gửi báo cáo sự cố mới, có thể kèm 1 ảnh. |
| UC3 | Theo dõi sự cố của tôi | Người báo | Xem danh sách và chi tiết các sự cố mình đã báo, cùng lịch sử xử lý. |
| UC4 | Trao đổi về sự cố | Người báo, Cán bộ CNTT | Nhắn qua lại không giới hạn số lần trên một sự cố, có thể kèm file. |
| UC5 | Gửi phản hồi sau hoàn thành | Người báo | Đánh giá mức hài lòng 1–5 sao sau khi sự cố `HOÀN_THÀNH`. |
| UC6 | Tiếp nhận và xử lý sự cố | Cán bộ CNTT | Xem danh sách, đổi trạng thái, ghi nguyên nhân/cách khắc phục. |
| UC7 | Lưu kinh nghiệm xử lý | Cán bộ CNTT | Tạo bài kinh nghiệm mới ngay từ nội dung xử lý vừa nhập (extend của UC6). |
| UC8 | Tra cứu kho kinh nghiệm | Người báo, Cán bộ CNTT | Tìm và xem bài kinh nghiệm theo danh mục/từ khóa. |
| UC9 | Quản lý kho kinh nghiệm | Cán bộ CNTT | Thêm/sửa/xóa bài kinh nghiệm. |
| UC10 | Quản lý danh mục sự cố | Cán bộ CNTT | Thêm/sửa/xóa danh mục dùng chung cho sự cố và kinh nghiệm. |
| UC11 | Quản lý phòng | Cán bộ CNTT | Thêm/sửa/xóa phòng. |
| UC12 | Quản lý thiết bị | Cán bộ CNTT | Thêm/sửa/xóa thiết bị. |
| UC13 | Nhập thiết bị từ Excel | Cán bộ CNTT | Nhập hàng loạt thiết bị từ file `.xlsx` (extend của UC12). |
| UC14 | Quản lý người dùng | Cán bộ CNTT | Thêm/sửa tài khoản, đổi vai trò, đặt lại mật khẩu, vô hiệu hóa/kích hoạt lại. |
| UC15 | Xem thống kê | Cán bộ CNTT | Xem số liệu và biểu đồ tổng quan tình hình xử lý sự cố. |
| UC16 | Xuất Excel danh sách sự cố | Cán bộ CNTT | Xuất danh sách sự cố đang lọc ra file Excel (extend của UC15/UC6). |

### 1.7. Đặc tả Use Case tiêu biểu

#### UC2 — Báo sự cố

| Mục | Nội dung |
|---|---|
| Actor | Người báo |
| Mục tiêu | Ghi nhận một sự cố mới để cán bộ CNTT xử lý. |
| Tiền điều kiện | Đã đăng nhập với vai trò USER. |
| Luồng chính | 1. Người dùng chọn thiết bị liên quan (tùy chọn), danh mục sự cố, mức ưu tiên, nhập tiêu đề/mô tả, có thể đính kèm 1 ảnh. 2. Hệ thống kiểm tra dữ liệu hợp lệ. 3. Hệ thống lưu sự cố mới ở trạng thái `MỚI`, gắn người báo là người dùng hiện tại. 4. Hệ thống báo đã ghi nhận. |
| Luồng thay thế/ngoại lệ | 2a. Thiếu tiêu đề hoặc mức ưu tiên không hợp lệ → báo lỗi, không lưu. 3a. File đính kèm sai định dạng (không phải JPG/PNG/GIF/WEBP) → báo lỗi, không lưu. 3b. File vượt 5MB → báo lỗi "File quá lớn", không lưu. |
| Hậu điều kiện | Một dòng mới trong danh sách sự cố ở trạng thái `MỚI`, xuất hiện ngay trong danh sách của cán bộ CNTT (UC6). |

#### UC6 — Tiếp nhận và xử lý sự cố (kèm UC7 — Lưu kinh nghiệm xử lý)

| Mục | Nội dung |
|---|---|
| Actor | Cán bộ CNTT |
| Mục tiêu | Cập nhật tiến độ xử lý một sự cố, ghi lại nguyên nhân và cách khắc phục. |
| Tiền điều kiện | Đã đăng nhập với vai trò ADMIN; sự cố tồn tại. |
| Luồng chính | 1. Cán bộ CNTT mở chi tiết một sự cố. 2. Chọn trạng thái mới, nhập nguyên nhân/cách khắc phục, có thể chọn 1 bài kinh nghiệm có sẵn để gắn vào. 3. Hệ thống lưu cập nhật; nếu trạng thái thực sự đổi thì tự ghi 1 dòng lịch sử; nếu chuyển sang `HOÀN_THÀNH` thì tự gán ngày hoàn thành. |
| Luồng thay thế/ngoại lệ (UC7, extend) | 2a. Nếu tick "lưu vào kho kinh nghiệm" mà **không** chọn bài có sẵn: phải nhập đủ nguyên nhân, cách khắc phục và chọn danh mục kho; hệ thống tạo một bài kinh nghiệm mới (dùng chính tiêu đề/mô tả của sự cố) rồi tự gắn bài đó vào sự cố đang xử lý. 2b. Thiếu nguyên nhân/cách khắc phục/danh mục khi đã tick "lưu vào kho" → báo lỗi, không lưu cập nhật. |
| Luồng thay thế/ngoại lệ (khác) | 1a. Trạng thái gửi lên không nằm trong {MỚI, ĐANG_XỬ_LÝ, HOÀN_THÀNH} → báo lỗi "Trạng thái không hợp lệ". |
| Hậu điều kiện | Sự cố có trạng thái/nguyên nhân/cách khắc phục mới; có thêm 1 dòng lịch sử xử lý; (nếu UC7 xảy ra) kho kinh nghiệm có thêm 1 bài mới. |

#### UC5 — Gửi phản hồi sau hoàn thành

| Mục | Nội dung |
|---|---|
| Actor | Người báo |
| Mục tiêu | Đánh giá mức độ hài lòng về cách xử lý sự cố của chính mình. |
| Tiền điều kiện | Sự cố thuộc về người dùng hiện tại và đã ở trạng thái `HOÀN_THÀNH`. |
| Luồng chính | 1. Người dùng chọn mức hài lòng (1–5), có thể nhập nhận xét. 2. Hệ thống lưu phản hồi, gắn với sự cố. |
| Luồng thay thế/ngoại lệ | 1a. Sự cố chưa `HOÀN_THÀNH` → báo lỗi, không cho gửi. 1b. Mức hài lòng ngoài khoảng 1–5 → báo lỗi. 2a. Sự cố đã có phản hồi trước đó (ràng buộc duy nhất ở CSDL) → hệ thống bắt lỗi và báo "Sự cố này đã được phản hồi rồi" thay vì để lộ lỗi SQL. |
| Hậu điều kiện | Sự cố có đúng 1 bản ghi phản hồi. |

#### UC4 — Trao đổi về sự cố

| Mục | Nội dung |
|---|---|
| Actor | Người báo, Cán bộ CNTT |
| Mục tiêu | Trao đổi thông tin bổ sung qua lại trên một sự cố cụ thể, không chờ xử lý xong. |
| Tiền điều kiện | Đã đăng nhập; với Người báo, sự cố phải thuộc về mình. |
| Luồng chính | 1. Người gửi nhập nội dung và/hoặc chọn 1 file đính kèm. 2. Hệ thống lưu tin nhắn kèm thời gian gửi. 3. Phía còn lại thấy tin nhắn mới khi mở lại trang chi tiết (trang tự kiểm tra tin mới định kỳ). |
| Luồng thay thế/ngoại lệ | 1a. Không nhập nội dung và không chọn file → báo lỗi "Nhập nội dung hoặc chọn file để gửi". 1b. File sai định dạng hoặc vượt 5MB → báo lỗi tương ứng. |
| Hậu điều kiện | Thêm 1 dòng trao đổi mới cho sự cố, sắp theo thời gian gửi. |

### 1.8. Yêu cầu phi chức năng (NFR)

| NFR | Mô tả | Thể hiện trong hệ thống |
|---|---|---|
| Bảo mật mật khẩu | Không lưu mật khẩu dạng rõ. | Băm PBKDF2-HMAC-SHA256 + salt ngẫu nhiên (`util/MatKhau.java`) trước khi lưu. |
| Phân quyền theo vai trò | Người dùng thường không được vào khu vực quản trị. | `AuthInterceptor` chặn truy cập `/admin/**` nếu không phải ADMIN, và chặn mọi route nếu chưa đăng nhập. |
| Khả năng dùng trên di động | Giao viên/sinh viên có thể báo sự cố ngay từ điện thoại. | Giao diện Bootstrap 5 responsive, bảng tự xếp thành thẻ dưới 768px. |
| Toàn vẹn dữ liệu tối thiểu | Email, tên phòng, tên thiết bị không được trùng; không phá vỡ khóa ngoại khi xóa. | Ràng buộc UNIQUE/CHECK/FK ở CSDL, kiểm tra trước ở tầng ứng dụng để báo lỗi dễ hiểu hơn mã lỗi Oracle. |
| Giới hạn tài nguyên tải lên | Tránh chiếm quá nhiều dung lượng đĩa. | Giới hạn loại file và dung lượng tối đa 5MB/file (`XuLyLoiChung`, `BoLuuTru`). |

---

## CHƯƠNG 2. MÔ HÌNH PHÂN TÍCH

### 2.1. Mô hình khái niệm (Conceptual Model)

Ở mức phân tích, mô hình chỉ thể hiện các khái niệm nghiệp vụ và quan hệ giữa chúng — **không** đưa khóa kỹ
thuật (ID), kiểu dữ liệu CSDL hay chi tiết cài đặt vào đây (những thứ đó thuộc Chương 3). Tên lớp dùng thuật
ngữ nghiệp vụ (ví dụ "Lần xử lý", "Trao đổi") thay vì tên bảng CSDL. Quan hệ vẽ bằng đường liên kết
(association) không mũi tên, đúng ký hiệu của mô hình miền; dấu ▶ sau tên quan hệ chỉ chiều đọc (ví dụ
"Phong *chứa ▶* ThietBi").

```mermaid
classDiagram
    direction TB
    class NguoiDung {
        Họ tên
        Email
        Vai trò
        Đơn vị
        Số điện thoại
        Đang hoạt động?
    }
    class SuCo {
        Tiêu đề
        Mô tả
        Mức ưu tiên
        Trạng thái
        Nguyên nhân
        Cách khắc phục
        Ngày báo
        Ngày hoàn thành
    }
    class ThietBi {
        Tên thiết bị
        Loại thiết bị
        Tình trạng
        Nhà cung cấp
        Ngày lắp đặt
    }
    class Phong {
        Tên phòng
        Tòa nhà
        Tầng
        Loại phòng
    }
    class DanhMucSuCo {
        Tên danh mục
        Mô tả
    }
    class BaiKinhNghiem {
        Tiêu đề
        Triệu chứng
        Nguyên nhân
        Giải pháp
    }
    class LanXuLy {
        Trạng thái cũ
        Trạng thái mới
        Ghi chú
        Thời điểm
    }
    class PhanHoi {
        Mức hài lòng
        Nội dung
    }
    class TraoDoi {
        Nội dung
        Tệp đính kèm
        Thời điểm
    }
    Phong "1" -- "0..*" ThietBi : chứa ▶
    ThietBi "0..1" -- "0..*" SuCo : liên quan ▶
    DanhMucSuCo "1" -- "0..*" SuCo : phân loại ▶
    DanhMucSuCo "1" -- "0..*" BaiKinhNghiem : phân loại ▶
    BaiKinhNghiem "0..1" -- "0..*" SuCo : tham chiếu ▶
    NguoiDung "1" -- "0..*" BaiKinhNghiem : viết ▶
    NguoiDung "1" -- "0..*" SuCo : báo ▶
    NguoiDung "0..1" -- "0..*" SuCo : xử lý ▶
    SuCo "1" -- "0..*" LanXuLy : có lịch sử ▶
    SuCo "1" -- "0..1" PhanHoi : được phản hồi ▶
    SuCo "1" -- "0..*" TraoDoi : có trao đổi ▶
    NguoiDung "1" -- "0..*" PhanHoi : gửi ▶
    NguoiDung "1" -- "0..*" TraoDoi : gửi ▶
```

**Giải thích các quyết định:**

- `LanXuLy` là một khái niệm có ý nghĩa riêng (một lần đổi trạng thái, với thời điểm và người cập nhật),
  không gộp vào `SuCo` vì một sự cố có **nhiều** lần đổi trạng thái theo thời gian — cần lưu thông tin riêng
  (quan hệ "có lịch sử" là quan hệ 1-nhiều có dữ liệu riêng cho từng lần).
- `PhanHoi` và `TraoDoi` tách thành 2 lớp riêng dù cùng là "phản ứng của người báo trên một sự cố", vì quy
  tắc nghiệp vụ khác hẳn nhau: `PhanHoi` chỉ 1 lần/sự cố và chỉ sau khi hoàn thành (bội số `0..1`), còn
  `TraoDoi` không giới hạn số lần và không ràng buộc trạng thái (bội số `0..*`).
- `ThietBi` liên kết tới `SuCo` là `0..1` (phía ThietBi) vì một sự cố **không bắt buộc** gắn thiết bị (ví dụ
  sự cố quên mật khẩu không liên quan thiết bị vật lý nào).
- Không đưa "Tài khoản AI/hệ thống ngoài" vào mô hình vì nằm ngoài phạm vi (§1.1).

### 2.2. Phân bổ trách nhiệm (CRC)

| Lớp | Trách nhiệm (Responsibility) | Cộng tác (Collaborator) |
|---|---|---|
| **SuCo** | Biết trạng thái, mức ưu tiên, nguyên nhân/cách khắc phục của chính nó; biết liệu đã `HOÀN_THÀNH` hay chưa (điều kiện để cho phép `PhanHoi`). | `NguoiDung` (người báo, người xử lý), `ThietBi`, `DanhMucSuCo`, `BaiKinhNghiem`, `LanXuLy`, `PhanHoi`, `TraoDoi` |
| **NguoiDung** | Biết vai trò của mình (USER/ADMIN) để hệ thống quyết định được đi tới màn hình nào; là Information Expert cho dữ liệu đăng nhập. | `SuCo` (báo/xử lý), `BaiKinhNghiem` (viết), `PhanHoi`/`TraoDoi` (gửi) |
| **BaiKinhNghiem** | Lưu lại một cách xử lý đã được tổng kết (triệu chứng → nguyên nhân → giải pháp) theo danh mục, để tra cứu lại. | `DanhMucSuCo`, `NguoiDung` (người viết), `SuCo` (sự cố tham chiếu tới nó) |
| **LanXuLy** | Ghi lại một lần đổi trạng thái: trạng thái cũ, trạng thái mới, thời điểm — phục vụ xem lại lịch sử và thống kê thời gian xử lý. | `SuCo` |
| **ThietBi** | Biết tình trạng hoạt động của chính nó; thuộc về đúng 1 `Phong`. | `Phong`, `SuCo` |
| **DanhMucSuCo** | Phân loại dùng chung cho cả `SuCo` lẫn `BaiKinhNghiem`, giúp liên kết "sự cố loại X" với "kinh nghiệm xử lý loại X". | `SuCo`, `BaiKinhNghiem` |

Nguyên tắc GRASP áp dụng rõ nhất ở đây là **Information Expert**: `SuCo` được giao trách nhiệm tự biết
mình đã hoàn thành hay chưa (thay vì để Controller so sánh chuỗi trạng thái), vì chính `SuCo` là nơi nắm dữ
liệu `trangThai` — chi tiết cài đặt tương ứng (`SuCo.daHoanThanh()`) trình bày ở §3.5.

### 2.3. System Sequence Diagram (SSD)

SSD chỉ thể hiện tương tác **Actor ↔ Hệ thống** như một hộp đen, chưa phân rã nội bộ (phần phân rã nội bộ
thuộc Chương 3).

**UC2 — Báo sự cố:**

```mermaid
sequenceDiagram
    actor U as Người báo
    participant S as :HệThống
    U->>S: baoSuCo(tieuDe, moTa, maThietBi, maDanhMuc, mucUuTien, anh)
    S-->>U: xácNhận("Đã gửi báo cáo sự cố")
```

**UC6 — Tiếp nhận và xử lý sự cố:**

```mermaid
sequenceDiagram
    actor A as Cán bộ CNTT
    participant S as :HệThống
    A->>S: xemDanhSáchSựCố(lọcTrạngThái?, từKhóa?)
    S-->>A: danhSáchSựCố
    A->>S: xemChiTiết(maSuCo)
    S-->>A: chiTiếtSựCố, lịchSửXửLý
    A->>S: capNhatXuLy(maSuCo, trangThaiMoi, nguyenNhan, cachKhacPhuc, luuKho?)
    S-->>A: xácNhận("Đã lưu cập nhật sự cố")
```

### 2.4. Điểm mơ hồ và cách nhóm quyết định xử lý

- **Thứ tự trạng thái không bị ép buộc:** đề bài gốc không nói rõ việc đổi trạng thái có phải đi tuần tự
  `MỚI → ĐANG_XỬ_LÝ → HOÀN_THÀNH` hay không. Nhóm **giả định** quy trình thực tế là tuần tự (R1), nhưng
  **quyết định không ép buộc ở tầng ứng dụng** để cán bộ CNTT có thể xử lý nhanh một sự cố đơn giản (đổi
  thẳng từ `MỚI` sang `HOÀN_THÀNH`) mà không phải thao tác 2 bước. Đây là đánh đổi linh hoạt-vs-chặt chẽ cần
  nêu rõ khi bảo vệ (xem thêm State Machine ở §3.4).
- **Một thiết bị có thể liên quan nhiều sự cố cùng lúc:** đề bài không giới hạn; nhóm giả định một thiết bị
  "đang hỏng" vẫn có thể bị báo thêm sự cố mới (không khóa báo cáo trùng), vì nhiều người có thể phát hiện
  cùng một vấn đề độc lập với nhau.
- **"Lưu vào kho kinh nghiệm" là tùy chọn, không bắt buộc:** nhóm quyết định không ép mọi sự cố `HOÀN_THÀNH`
  đều phải sinh ra bài kinh nghiệm, vì nhiều sự cố (ví dụ quên mật khẩu) không có giá trị tra cứu lại.

---

## CHƯƠNG 3. THIẾT KẾ HỆ THỐNG

### 3.1. Kiến trúc và phân tầng trách nhiệm

Hệ thống dùng kiến trúc 3 tầng theo tư duy Boundary–Control–Entity, ánh xạ sang Spring MVC như sau (không
có tầng Service riêng — Controller đồng thời đóng vai trò điều phối/Control, vì nghiệp vụ ở quy mô đồ án
chưa đủ phức tạp để tách thêm một tầng; đây là lựa chọn có chủ đích, không phải thiếu sót):

| Vai trò BCE | Thành phần | Trách nhiệm |
|---|---|---|
| Boundary (giao diện) | Template Thymeleaf (`templates/*.html`) | Hiển thị dữ liệu, nhận input form, không chứa logic nghiệp vụ. |
| Control (điều phối) | `*Controller` (`SuCoController`, `AdminController`, `AuthController`...) | Tiếp nhận HTTP request, kiểm tra hợp lệ ở mức bề mặt (ví dụ trường bắt buộc), gọi Repository tương ứng, chọn view/redirect trả về. |
| Entity (miền nghiệp vụ) | `model/*` (`SuCo`, `NguoiDung`...) + `*Repo` (`SuCoRepo`...) | Model giữ dữ liệu và một số quy tắc gắn liền với chính nó (`SuCo.daHoanThanh()`); Repo là nơi duy nhất biết cấu trúc bảng/CSDL (Information Expert cho việc đọc/ghi dữ liệu). |
| Cắt ngang (cross-cutting) | `AuthInterceptor`, `XuLyLoiChung` | Xác thực/phân quyền và xử lý lỗi tập trung cho **mọi** controller, tách khỏi từng Controller cụ thể (Pure Fabrication — xem §3.5). |

### 3.2. Sequence Diagram (mức thiết kế)

**UC2 — Báo sự cố:**

```mermaid
sequenceDiagram
    actor U as Người báo
    participant C as sc:SuCoController
    participant L as anh:LuuAnhSuCo
    participant R as repo:SuCoRepo
    participant DB as Oracle (SU_CO)

    U->>C: POST /user/bao-cao(tieuDe, moTa, maTb, maDm, mucUuTien, anh)
    alt tiêu đề rỗng hoặc mức ưu tiên không hợp lệ
        C-->>U: redirect /user (flash "loi")
    else hợp lệ
        C->>L: luu(anh)
        alt loại file không hợp lệ
            L-->>C: throw FileKhongHopLeException
            C-->>U: redirect /user (flash "loi")
        else hợp lệ
            L-->>C: duongDanAnh
            C->>R: taoMoi(tieuDe, moTa, maNguoiBao, maTb, maDm, mucUuTien, duongDanAnh)
            R->>DB: INSERT INTO SU_CO (...) VALUES (..., 'MOI', ...)
            DB-->>R: OK
            R-->>C: void
            C-->>U: redirect /user (flash "thongBao")
        end
    end
```

**UC6 + UC7 — Xử lý sự cố, kèm nhánh tự tạo bài kinh nghiệm:**

```mermaid
sequenceDiagram
    actor Ad as Cán bộ CNTT
    participant C as admin:AdminController
    participant KR as kn:KinhNghiemRepo
    participant SR as repo:SuCoRepo
    participant DB as Oracle

    Ad->>C: POST /admin/{maSc}(trangThai, nguyenNhan, cachKhacPhuc, maBaiKn?, luuKho?, maDmKho?)
    alt trạng thái không hợp lệ
        C-->>Ad: redirect /admin/{maSc} (flash "loi")
    else hợp lệ
        opt luuKho = true và chưa chọn bài có sẵn
            C->>SR: timTheoMa(maSc)
            SR->>DB: SELECT ... FROM V_SU_CO_CHI_TIET WHERE MA_SC = ?
            DB-->>SR: dòng sự cố
            SR-->>C: suCo
            C->>KR: taoMoi(tieuDe, maDmKho, moTa, nguyenNhan, cachKhacPhuc, maNguoiViet)
            KR->>DB: INSERT INTO BAI_KINH_NGHIEM (...)
            DB-->>KR: maBai (IDENTITY tự sinh)
            KR-->>C: maBaiGan
        end
        C->>SR: capNhat(maSc, trangThai, nguyenNhan, cachKhacPhuc, maBaiGan, maAdminXuLy, ghiChu)
        SR->>DB: UPDATE SU_CO SET TRANG_THAI=?, NGUYEN_NHAN=?, ...
        DB->>DB: trigger TRG_SU_CO_HOAN_THANH (gán/xóa NGAY_HOAN_THANH)
        DB->>DB: trigger TRG_SU_CO_LICH_SU (INSERT LICH_SU_XU_LY)
        DB-->>SR: OK
        SR-->>C: true
        C-->>Ad: redirect /admin/{maSc} (flash "thongBao")
    end
```

**UC5 — Gửi phản hồi (minh họa biến ràng buộc CSDL thành thông báo dễ hiểu):**

```mermaid
sequenceDiagram
    actor U as Người báo
    participant C as sc:SuCoController
    participant PR as ph:PhanHoiRepo
    participant DB as Oracle (PHAN_HOI)

    U->>C: POST /user/{maSc}/phan-hoi(mucHaiLong, noiDung)
    C->>C: kiểm tra suCo.daHoanThanh() và 1 <= mucHaiLong <= 5
    alt chưa hoàn thành hoặc mức không hợp lệ
        C-->>U: redirect /user/{maSc} (flash "loi")
    else hợp lệ
        C->>PR: taoMoi(maSc, maNguoiPhanHoi, mucHaiLong, noiDung)
        PR->>DB: INSERT INTO PHAN_HOI (...)
        alt đã có phản hồi trước đó (UQ_PHAN_HOI_SU_CO)
            DB-->>PR: ORA-00001 (vi phạm ràng buộc duy nhất)
            PR-->>C: throw DuplicateKeyException
            C-->>U: redirect /user/{maSc} (flash "Sự cố này đã được phản hồi rồi")
        else chưa có
            DB-->>PR: OK
            PR-->>C: void
            C-->>U: redirect /user/{maSc} (flash "thongBao")
        end
    end
```

### 3.3. Design Class Diagram (DCD)

DCD được tách thành 3 hình nhỏ theo từng nhóm trách nhiệm thay vì dồn vào 1 hình (Mermaid tự dàn trang, và
khi gộp chung thì các mũi tên đi xuyên qua lớp khác, gây hiểu nhầm về phụ thuộc). Quy ước chung cho cả 3
hình:

- Một mũi tên liền `-->` có bội số `1` nghĩa là lớp nguồn giữ một thuộc tính `private final` kiểu lớp đích,
  được Spring tiêm qua constructor. Thuộc tính đó không lặp lại trong ô thuộc tính; ô thuộc tính chỉ liệt kê
  các repo không có mặt trên hình.
- Chữ ký phương thức của Controller lược bỏ tham số hạ tầng của Spring (`HttpSession`, `Model`,
  `RedirectAttributes`) để dòng không quá dài; chữ ký đầy đủ xem trong mã nguồn.

**Hình 3.3a — Luồng Controller → Repository → đối tượng nghiệp vụ:**

```mermaid
classDiagram
    direction LR
    class SuCoController {
        -ThietBiRepo thietBiRepo
        -DanhMucRepo danhMucRepo
        -BinhLuanRepo binhLuanRepo
        +trangNguoiDung() String
        +chiTietCuaToi(maSc) String
        +baoSuCo(tieuDe, moTa, maTb, maDm, mucUuTien, anh) String
        +guiBinhLuan(maSc, noiDung, tep) String
        +guiPhanHoi(maSc, mucHaiLong, noiDung) String
    }
    class AdminController {
        -DanhMucRepo danhMucRepo
        -PhanHoiRepo phanHoiRepo
        -BinhLuanRepo binhLuanRepo
        +danhSach(trangThai, q, trang) String
        +chiTiet(maSc) String
        +capNhat(maSc, trangThai, nguyenNhan, cachKhacPhuc, maBaiKn, luuKho, maDmKho, ghiChu) String
        +xoa(maSc) String
    }
    class SuCoRepo {
        +taoMoi(tieuDe, moTa, maNguoiBao, maTb, maDm, mucUuTien, anh) void
        +timTheoNguoiBao(maNd) List~SuCo~
        +timTheoMa(maSc) Optional~SuCo~
        +capNhat(maSc, trangThai, nguyenNhan, cachKhacPhuc, maBaiKn, maAdmin, ghiChu) boolean
        +xoa(maSc) void
        +timSuCoHayGap() List~SuCoHayGap~
        +timLichSu(maSc) List~LichSu~
    }
    class KinhNghiemRepo {
        +taoMoi(tieuDe, maDm, trieuChung, nguyenNhan, giaiPhap, maNguoiViet) long
        +capNhat(maBai, tieuDe, maDm, trieuChung, nguyenNhan, giaiPhap) void
        +xoa(maBai) void
    }
    class PhanHoiRepo {
        +timTheoSuCo(maSc) Optional~PhanHoi~
        +taoMoi(maSc, maNguoiPhanHoi, mucHaiLong, noiDung) void
    }
    class SuCo {
        -Long maSc
        -String tieuDe
        -MucUuTien mucUuTien
        +daHoanThanh() boolean
    }
    class TrangThaiSuCo {
        <<enumeration>>
        MOI
        DANG_XU_LY
        HOAN_THANH
    }
    SuCoController --> "1" PhanHoiRepo
    SuCoController --> "1" SuCoRepo
    AdminController --> "1" SuCoRepo
    AdminController --> "1" KinhNghiemRepo
    SuCoRepo ..> SuCo : «create»
    SuCo --> "1" TrangThaiSuCo
```

**Hình 3.3b — Lưu tệp tải lên (Template Method):**

```mermaid
classDiagram
    direction TB
    class SuCoController
    class AdminController
    class BoLuuTru {
        <<abstract>>
        -Path thuMuc
        +luu(file) String
        #duoiHopLe()* Map~String,String~
        #thongBaoLoiLoai()* String
    }
    class LuuAnhSuCo {
        #duoiHopLe() Map~String,String~
        #thongBaoLoiLoai() String
    }
    class LuuTepTraoDoi {
        #duoiHopLe() Map~String,String~
        #thongBaoLoiLoai() String
    }
    SuCoController --> "1" LuuAnhSuCo
    SuCoController --> "1" LuuTepTraoDoi
    AdminController --> "1" LuuTepTraoDoi
    BoLuuTru <|-- LuuAnhSuCo
    BoLuuTru <|-- LuuTepTraoDoi
```

**Hình 3.3c — Kiểu nhãn dùng chung và lớp hạ tầng web:**

```mermaid
classDiagram
    direction TB
    class CoNhan {
        <<interface>>
        +getNhan() String
        +getLopBadge() String
    }
    class TrangThaiSuCo {
        <<enumeration>>
        MOI
        DANG_XU_LY
        HOAN_THANH
    }
    class AuthInterceptor {
        +preHandle(req, resp, handler) boolean
    }
    class XuLyLoiChung {
        +duLieuKhongHopLe(request) String
        +fileQuaLon(request) String
    }
    CoNhan <|.. TrangThaiSuCo
```

`AuthInterceptor` (chặn truy cập theo vai trò trước khi vào Controller) và `XuLyLoiChung`
(`@ControllerAdvice` xử lý lỗi chung) không có liên kết trực tiếp tới lớp nào khác vì Spring gọi chúng
theo cấu hình, không qua tham chiếu trong code.

*(Danh sách thuộc tính/phương thức đầy đủ của từng lớp xem trực tiếp trong `src/main/java/vn/utc/suco/`; sơ
đồ trên chỉ chọn các thành viên liên quan tới 3 UC đã phân tích ở §3.2 để tránh sơ đồ quá tải.)*

### 3.4. State Machine — vòng đời `SuCo.trangThai`

```mermaid
stateDiagram-v2
    direction LR

    [*] --> MOI : (1) báo sự cố
    MOI --> DANG_XU_LY : (2) tiếp nhận
    DANG_XU_LY --> HOAN_THANH : (3) hoàn thành
    HOAN_THANH --> DANG_XU_LY : (4) mở lại
    MOI --> HOAN_THANH : (5) xử lý ngay
    DANG_XU_LY --> MOI : (6) đặt lại
    HOAN_THANH --> MOI : (7) đặt lại
```

| # | Chuyển trạng thái | Sự kiện / ý nghĩa | Hiệu ứng phụ (trigger CSDL) |
|---|---|---|---|
| (1) | → `MOI` | Người báo gửi sự cố (UC2) | — |
| (2) | `MOI` → `DANG_XU_LY` | Cán bộ CNTT tiếp nhận xử lý | Ghi 1 dòng `LanXuLy` |
| (3) | `DANG_XU_LY` → `HOAN_THANH` | Hoàn thành xử lý | Gán `NGAY_HOAN_THANH`; ghi 1 dòng `LanXuLy` |
| (4) | `HOAN_THANH` → `DANG_XU_LY` | Mở lại xử lý | Xóa `NGAY_HOAN_THANH`; ghi 1 dòng `LanXuLy` |
| (5) | `MOI` → `HOAN_THANH` | Xử lý ngay, bỏ qua bước trung gian | Gán `NGAY_HOAN_THANH`; ghi 1 dòng `LanXuLy` |
| (6) | `DANG_XU_LY` → `MOI` | Đặt lại "mới" (hiếm dùng) | Ghi 1 dòng `LanXuLy` |
| (7) | `HOAN_THANH` → `MOI` | Đặt lại "mới" (hiếm dùng) | Xóa `NGAY_HOAN_THANH`; ghi 1 dòng `LanXuLy` |

Việc gán/xóa `NGAY_HOAN_THANH` do trigger `TRG_SU_CO_HOAN_THANH` đảm nhận, còn việc ghi dòng `LanXuLy` do
trigger `TRG_SU_CO_LICH_SU` đảm nhận. Chỉ ở trạng thái `HOAN_THANH` người báo mới gửi được `PhanHoi` (UC5),
và chỉ gửi được đúng 1 lần.

Sơ đồ phản ánh **đúng những gì mã nguồn hiện cho phép** (`AdminController.capNhat` chỉ kiểm tra trạng thái
gửi lên có nằm trong 3 giá trị hợp lệ, không kiểm tra trạng thái hiện tại), chứ không chỉ vẽ quy trình lý
tưởng tuyến tính — đây chính là điểm mơ hồ đã nêu ở §2.4. Nếu nhóm muốn siết lại (chỉ cho phép đi theo đúng
thứ tự `MỚI → ĐANG_XỬ_LÝ → HOÀN_THÀNH`), cách làm hợp lý là thêm điều kiện kiểm tra trạng thái hiện tại
ngay trong `SuCoRepo.capNhat` hoặc một lớp kiểm tra chuyển trạng thái riêng — nên nêu đây là hướng cải tiến
ở Chương 4 (giới hạn sản phẩm) nếu nhóm chưa kịp làm.

### 3.5. Giải thích quyết định thiết kế theo GRASP/SOLID

| Nguyên tắc | Áp dụng ở đâu | Giải thích |
|---|---|---|
| **Information Expert** | `SuCo.daHoanThanh()`; `SuCoRepo` là nơi duy nhất viết SQL cho bảng `SU_CO`. | `SuCo` tự biết trạng thái của chính nó nên tự trả lời được câu hỏi "đã xong chưa" thay vì Controller phải so sánh chuỗi `"HOAN_THANH"` rải rác nhiều nơi; `SuCoRepo` nắm cấu trúc bảng/view nên là nơi hợp lý nhất để đọc/ghi dữ liệu sự cố. |
| **Creator** | `AdminController.capNhat` quyết định **thời điểm** cần tạo `BaiKinhNghiem` mới (khi `luuKho=true`), nhưng giao việc tạo thật sự cho `KinhNghiemRepo.taoMoi`. | Controller biết ngữ cảnh nghiệp vụ (đang xử lý sự cố nào, admin nào) nên hợp lý để quyết định *khi nào* tạo, còn `KinhNghiemRepo` nắm cấu trúc bảng `BAI_KINH_NGHIEM` nên là nơi hợp lý để *thực hiện* việc tạo. |
| **Controller (GRASP)** | `SuCoController`, `AdminController`, `AuthController`. | Mỗi controller tiếp nhận request của đúng một nhóm UC liên quan (báo/theo dõi sự cố, xử lý sự cố của admin, đăng nhập) thay vì dùng 1 controller khổng lồ xử lý mọi request — tương ứng nguyên tắc chọn Controller theo "use-case controller". |
| **Polymorphism + OCP** | `BoLuuTru` (abstract, Template Method `luu()`) với 2 lớp con `LuuAnhSuCo`/`LuuTepTraoDoi`. | Phần lưu file xuống đĩa và sinh tên file là *chung*, chỉ khác ở danh sách loại file được phép và thông báo lỗi. Muốn thêm một loại lưu trữ mới (ví dụ file đính kèm cho bài kinh nghiệm) chỉ cần thêm 1 lớp con mới kế thừa `BoLuuTru`, **không sửa** `luu()` đã có — đúng tinh thần Open/Closed. |
| **Polymorphism + ISP** | Interface `CoNhan` (`getNhan()`, `getLopBadge()`) do 5 enum (`VaiTro`, `TrangThaiNguoiDung`, `TrangThaiSuCo`, `MucUuTien`, `TinhTrangThietBi`) cùng implement. | Giao diện Thymeleaf chỉ cần gọi `t.nhan`/`t.lopBadge` để hiển thị badge mà không cần biết đang cầm enum cụ thể nào — interface chỉ có đúng 2 phương thức cần dùng, không ép các enum phải cài thêm hành vi không liên quan. |
| **LSP** | `LuuAnhSuCo`/`LuuTepTraoDoi` dùng thay cho nhau tại chỗ gọi `BoLuuTru.luu(file)`. | Cả hai đều tuân thủ đúng hợp đồng của lớp cha: trả về URL hợp lệ hoặc ném `FileKhongHopLeException`/`IOException`, không có hành vi bất ngờ nào phá vỡ kỳ vọng của nơi gọi. |
| **Pure Fabrication + SRP** | `AuthInterceptor` (xác thực/phân quyền), `XuLyLoiChung` (`@ControllerAdvice` xử lý lỗi tập trung). | Hai mối quan tâm "ai được vào trang nào" và "lỗi dữ liệu/lỗi upload hiển thị thế nào" không gắn liền với một khái niệm nghiệp vụ cụ thể nào (không phải trách nhiệm tự nhiên của `SuCo` hay `NguoiDung`) — tách thành 2 lớp phụ trợ riêng để mọi Controller dùng chung, tránh lặp lại try-catch ở từng nơi. |
| **High Cohesion / Low Coupling** | Mỗi `*Repo` chỉ thao tác đúng 1 bảng/nhóm bảng liên quan trực tiếp của nó; `SuCoRepo` đọc từ `V_SU_CO_CHI_TIET` (view đã JOIN sẵn) thay vì tự JOIN 5 bảng ở tầng Java. | Gom việc JOIN xuống CSDL (nơi làm việc đó hiệu quả nhất) giữ cho `SuCoRepo` tập trung vào đúng một việc (đọc/ghi sự cố), giảm phụ thuộc chéo giữa các Repo. |
| **DIP — giới hạn đã biết** | Controller phụ thuộc trực tiếp vào lớp `*Repo` cụ thể (không qua interface). | Đây là đánh đổi có chủ đích cho quy mô đồ án (không cần 2 cách triển khai lưu trữ khác nhau nên interface sẽ thừa — tránh "nhiều interface cũng không tự động làm thiết kế tốt hơn" như hướng dẫn BTL đã lưu ý), nhưng nhóm cần nêu rõ đây là giới hạn khi bảo vệ, không che giấu. |

### 3.6. Thiết kế dữ liệu (ERD)

Sơ đồ ER đầy đủ (9 bảng, kèm bảng mô tả khóa chính/khóa ngoại và các ràng buộc) đã có sẵn ở
[SO_DO_QUAN_HE.md](SO_DO_QUAN_HE.md) — không lặp lại ở đây. Phần này chỉ nêu các điểm **ánh xạ không phải
1-1** từ mô hình khái niệm (§2.1) sang thiết kế dữ liệu, đúng tinh thần Mục 4.6 của hướng dẫn BTL:

- **`LanXuLy` → bảng `LICH_SU_XU_LY`, nhưng không có "LichSuController"/Repo nào ghi trực tiếp.** Đối
  tượng này được **CSDL tự sinh ra** (trigger `TRG_SU_CO_LICH_SU`), không phải tầng Java tạo — một ví dụ rõ
  cho việc "không phải mọi lớp phân tích đều cần một lớp Java tương ứng ở tầng cài đặt": trách nhiệm ghi
  lịch sử được đẩy xuống tầng CSDL vì nó phải đúng **mọi lúc** cột `TRANG_THAI` đổi, kể cả khi có ai đó sửa
  dữ liệu trực tiếp bằng SQL, không chỉ qua ứng dụng.
- **`SuCoHayGap` (model Java `vn.utc.suco.model.SuCoHayGap`) không map tới bảng nào** — nó map tới view
  `V_SU_CO_THUONG_GAP`, dữ liệu suy ra bằng `COUNT(...)` tại thời điểm truy vấn chứ không lưu cứng, để tránh
  phải tự đồng bộ một cột tần suất mỗi khi có sự cố mới (đúng khuyến cáo "không phải mọi lớp cần trở thành
  bảng").
- **Quan hệ phản thân của `NguoiDung`** (`MA_NGUOI_TAO`, `MA_NGUOI_XOA` — ai tạo/ai vô hiệu hóa tài khoản
  này) không xuất hiện trong mô hình khái niệm ở §2.1 vì đây là chi tiết vận hành/kiểm toán (audit), không
  phải khái niệm nghiệp vụ cốt lõi mà người dùng cuối quan tâm — chỉ được thêm vào ở mức thiết kế dữ liệu.
- **Bội số `0..1`/`1` ở mô hình khái niệm được hiện thực bằng ràng buộc `NOT NULL`/cho phép `NULL`** ở cột
  khóa ngoại tương ứng (ví dụ `SU_CO.MA_TB` cho phép NULL vì quan hệ ThietBi–SuCo là `0..1`), và bội số
  `0..1` "chỉ 1 bản ghi con" (như `SuCo`–`PhanHoi`) được hiện thực bằng ràng buộc `UNIQUE` trên khóa ngoại
  (`UQ_PHAN_HOI_SU_CO`) thay vì chỉ dựa vào quy ước ở tầng ứng dụng.

---

## CHƯƠNG 4. CÀI ĐẶT VÀ KIỂM THỬ

### 4.1. Cấu trúc dự án và công nghệ

Xem chi tiết đầy đủ ở [README.md](README.md) (môi trường, cách dựng CSDL, cách chạy). Tóm tắt:

- Spring Boot 4.1.1 (Spring MVC + Thymeleaf + JdbcTemplate), Java 21, Oracle Database (PDB `FREEPDB1`).
- `sql/` chứa toàn bộ script CSDL chạy một mạch bằng `@00_run_all.sql` (tablespace, 9 bảng, trigger, dữ
  liệu demo, 2 view).
- `src/main/java/vn/utc/suco/{model,repository,controller,config,util}` — đúng cấu trúc BCE đã mô tả ở
  §3.1.

### 4.2. Minh chứng cài đặt gắn với Use Case

| Use Case | Route | Controller | Template |
|---|---|---|---|
| UC1 Đăng nhập/Đăng xuất | `GET/POST /login`, `GET /logout` | `AuthController` | `login.html` |
| UC2 Báo sự cố | `POST /user/bao-cao` | `SuCoController.baoSuCo` | `user.html` |
| UC3 Theo dõi sự cố của tôi | `GET /user`, `GET /user/{maSc}` | `SuCoController` | `user.html`, `user-chi-tiet.html` |
| UC4 Trao đổi về sự cố | `POST /user/{maSc}/binh-luan`, `POST /admin/{maSc}/binh-luan` | `SuCoController`, `AdminController` | `user-chi-tiet.html`, `admin-chi-tiet.html` |
| UC5 Gửi phản hồi | `POST /user/{maSc}/phan-hoi` | `SuCoController.guiPhanHoi` | `user-chi-tiet.html` |
| UC6 Xử lý sự cố | `GET /admin`, `POST /admin/{maSc}` | `AdminController` | `admin.html`, `admin-chi-tiet.html` |
| UC7 Lưu kinh nghiệm xử lý | `POST /admin/{maSc}` (tham số `luuKho`) | `AdminController.capNhat` → `KinhNghiemRepo` | `admin-chi-tiet.html` |
| UC8 Tra cứu kho kinh nghiệm | `GET /kinh-nghiem`, `GET /kinh-nghiem/{maBai}` | `KinhNghiemController` | `kinh-nghiem.html`, `kinh-nghiem-chi-tiet.html` |
| UC9 Quản lý kho kinh nghiệm | `/admin/kinh-nghiem/**` | `KinhNghiemController` | `kinh-nghiem-quan-ly.html`, `kinh-nghiem-sua.html` |
| UC10 Quản lý danh mục | `/admin/danh-muc/**` | `DanhMucController` | `danh-muc.html`, `danh-muc-sua.html` |
| UC11 Quản lý phòng | `/admin/phong/**` | `PhongController` | `phong.html`, `phong-sua.html` |
| UC12 Quản lý thiết bị | `/admin/thiet-bi/**` | `ThietBiController` | `thiet-bi.html`, `thiet-bi-sua.html` |
| UC13 Nhập thiết bị từ Excel | `POST /admin/thiet-bi/nhap-excel` | `ThietBiController` | `thiet-bi.html` |
| UC14 Quản lý người dùng | `/admin/nguoi-dung/**` | `NguoiDungController` | `nguoi-dung.html`, `nguoi-dung-sua.html` |
| UC15 Xem thống kê | `GET /admin/thong-ke` | `ThongKeController` | `thong-ke.html` |
| UC16 Xuất Excel | `GET /admin/xuat-excel` | `AdminController.xuatExcel` | *(tải file trực tiếp)* |

### 4.3. Kế hoạch test case

Bảng dưới là **kế hoạch** test case theo luồng chính và ngoại lệ đã đặc tả ở §1.7 — nhóm cần tự chạy ứng
dụng (theo README.md), điền cột "Kết quả thực tế" và ảnh chụp minh họa, không được để trống khi nộp (đúng
yêu cầu "không chỉ chụp giao diện; cần chứng minh quy tắc nghiệp vụ quan trọng hoạt động đúng").

| # | UC | Input | Điều kiện | Kết quả mong đợi | Kết quả thực tế |
|---|---|---|---|---|---|
| T1 | UC2 | Báo sự cố, bỏ trống tiêu đề | Luồng ngoại lệ 2a | Báo lỗi, không tạo sự cố mới | *(điền khi test)* |
| T2 | UC2 | Báo sự cố, đính kèm file `.pdf` làm ảnh | Luồng ngoại lệ 3a | Báo lỗi "Chỉ nhận ảnh JPG, PNG, GIF hoặc WEBP" | *(điền khi test)* |
| T3 | UC2 | Báo sự cố hợp lệ, đủ trường | Luồng chính | Sự cố mới xuất hiện ở `/admin` với trạng thái `MỚI` | *(điền khi test)* |
| T4 | UC6 | Đổi trạng thái `MỚI → ĐANG_XỬ_LÝ` | Luồng chính | Có thêm 1 dòng lịch sử; `NGAY_HOAN_THANH` vẫn `NULL` | *(điền khi test)* |
| T5 | UC6/UC7 | Đổi trạng thái sang `HOÀN_THÀNH`, tick "lưu vào kho" nhưng bỏ trống nguyên nhân | Luồng ngoại lệ 2b | Báo lỗi, không lưu cập nhật, không tạo bài kinh nghiệm | *(điền khi test)* |
| T6 | UC6/UC7 | Đổi trạng thái sang `HOÀN_THÀNH`, tick "lưu vào kho", điền đủ nguyên nhân/cách khắc phục/danh mục | Luồng thay thế 2a | `NGAY_HOAN_THANH` được gán tự động; có bài kinh nghiệm mới trong `/kinh-nghiem`; sự cố gắn với bài đó | *(điền khi test)* |
| T7 | UC5 | Gửi phản hồi khi sự cố còn `ĐANG_XỬ_LÝ` | Luồng ngoại lệ 1a | Báo lỗi "Chỉ gửi phản hồi được khi sự cố đã hoàn thành" | *(điền khi test)* |
| T8 | UC5 | Gửi phản hồi lần 2 cho cùng 1 sự cố đã `HOÀN_THÀNH` | Luồng ngoại lệ 2a | Báo lỗi "Sự cố này đã được phản hồi rồi", không tạo bản ghi trùng | *(điền khi test)* |
| T9 | UC11 | Thêm phòng trùng tên (cùng tòa, cùng tầng) | Quy tắc R6 | Báo lỗi trùng, không thêm | *(điền khi test)* |
| T10 | UC14 | Admin tự vô hiệu hóa chính tài khoản mình | Ràng buộc tự bảo vệ | Hệ thống từ chối thao tác | *(điền khi test)* |

### 4.4. Giới hạn sản phẩm

- Vòng đời trạng thái sự cố chưa bị ép buộc đi đúng thứ tự ở tầng ứng dụng (xem §2.4, §3.4) — có thể bổ
  sung guard nếu muốn chặt chẽ hơn.
- Chưa có thông báo chủ động (email/SMS) khi sự cố đổi trạng thái — người dùng phải tự vào xem.
- Một sự cố chỉ gắn được tối đa 1 thiết bị; chưa hỗ trợ sự cố liên quan nhiều thiết bị cùng lúc.
- Controller phụ thuộc trực tiếp lớp `Repo` cụ thể, không qua interface (xem đánh đổi DIP ở §3.5).

---

## KẾT LUẬN

Hệ thống đáp ứng được mục tiêu đặt ra ở §1.1: hỗ trợ đầy đủ quy trình báo cáo → xử lý → phản hồi → tích lũy
kinh nghiệm cho sự cố CNTT, có đủ các quy tắc nghiệp vụ và trường hợp ngoại lệ để phân tích sâu (không chỉ
là CRUD đơn thuần) — thể hiện qua các quyết định thiết kế đã giải thích ở Chương 3 (đặc biệt là Information
Expert ở `SuCoRepo`/`SuCo`, Polymorphism+OCP ở `BoLuuTru`, và việc đẩy một phần trách nhiệm toàn vẹn dữ liệu
xuống CSDL qua trigger). Giới hạn còn lại (nêu ở §4.4) là các hướng cải tiến khả thi cho các phiên bản sau,
không ảnh hưởng tới luồng nghiệp vụ chính.

*(Nhóm bổ sung thêm nhận xét riêng của nhóm về mức độ đáp ứng và những gì đã học được qua bài tập lớn này.)*

## TÀI LIỆU THAM KHẢO

1. Tài liệu "Hướng dẫn thực hiện Bài tập lớn — Học phần Phân tích và thiết kế hướng đối tượng (OOAD)".
2. Larman, C. — *Applying UML and Patterns* (nguồn gốc các nguyên tắc GRASP).
3. Mã nguồn và tài liệu nội bộ của chính dự án: [README.md](README.md), [SO_DO_QUAN_HE.md](SO_DO_QUAN_HE.md), `sql/`.

## PHỤ LỤC

### Bảng phân công và đóng góp

| Họ tên | MSSV | Nhiệm vụ phụ trách | % đóng góp |
|---|---|---|---|
| *(điền)* | *(điền)* | *(điền — ví dụ: Chương 1 + UC Diagram)* | *(điền)* |
| *(điền)* | *(điền)* | *(điền — ví dụ: Chương 2 + Conceptual Model/CRC)* | *(điền)* |
| *(điền)* | *(điền)* | *(điền — ví dụ: Chương 3 + Sequence/DCD/State Machine)* | *(điền)* |
| *(điền)* | *(điền)* | *(điền — ví dụ: Chương 4 + kiểm thử)* | *(điền)* |
