/*
 * Tìm nhanh ngay trên trang (không cần tải lại): ô input ghi
 *   data-loc="#idBang"  -> ẩn các dòng <tr> của bảng không khớp
 *   data-loc=".ds-bai"  -> ẩn các thẻ con không khớp của một danh sách
 * So sánh có bỏ dấu tiếng Việt, nên gõ "may in" vẫn tìm ra "Máy in".
 */
(function () {
  'use strict';

  function boDau(s) {
    return (s || '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
  }

  function khoiTao(o) {
    var khung = document.querySelector(o.getAttribute('data-loc'));
    if (!khung) {
      return;
    }
    var laBang = khung.tagName === 'TABLE';
    var cha = laBang ? khung.querySelector('tbody') : khung;
    if (!cha) {
      return;
    }

    // Dòng thông báo "không tìm thấy", tạo sẵn một lần rồi bật/tắt khi gõ
    var khongThay;
    if (laBang) {
      khongThay = document.createElement('tr');
      khongThay.innerHTML = '<td class="trong-rong" colspan="99">'
        + '<i class="bi bi-search"></i>Không có dòng nào khớp từ khóa</td>';
    } else {
      khongThay = document.createElement('div');
      khongThay.className = 'trong-rong';
      khongThay.innerHTML = '<i class="bi bi-search"></i>Không có kết quả nào khớp từ khóa';
    }
    khongThay.hidden = true;
    cha.appendChild(khongThay);

    function loc() {
      var tuKhoa = boDau(o.value.trim());
      var hien = 0;
      var tong = 0;
      Array.prototype.forEach.call(cha.children, function (dong) {
        if (dong === khongThay || dong.querySelector('.trong-rong') || dong.classList.contains('trong-rong')) {
          return;
        }
        tong++;
        var khop = !tuKhoa || boDau(dong.textContent).indexOf(tuKhoa) !== -1;
        dong.hidden = !khop;
        if (khop) {
          hien++;
        }
      });
      khongThay.hidden = !(tong > 0 && hien === 0);
    }

    o.addEventListener('input', loc);
    loc();
  }

  document.querySelectorAll('[data-loc]').forEach(khoiTao);
})();

/*
 * Ô "nhập lại" phải khớp với ô khác: <input data-khop="#matKhau">.
 * Dùng setCustomValidity nên trình duyệt chặn gửi form và hiện thông báo như các ô required khác.
 * (Chỉ để báo sớm cho người dùng, controller vẫn kiểm tra lại.)
 */
(function () {
  'use strict';

  document.querySelectorAll('[data-khop]').forEach(function (o) {
    var goc = document.querySelector(o.getAttribute('data-khop'));
    if (!goc) {
      return;
    }
    function kiemTra() {
      o.setCustomValidity(o.value === goc.value ? '' : 'Mật khẩu nhập lại không khớp');
    }
    o.addEventListener('input', kiemTra);
    goc.addEventListener('input', kiemTra);
  });
})();

/* Thoát HTML entity (dùng chung cho các khối polling AJAX dưới đây, vì nội dung chèn vào trang là do người
   dùng gõ - không thoát đúng cách sẽ hở lỗi XSS lưu trữ qua tiêu đề sự cố / nội dung trao đổi). */
function thoat(s) {
  return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}

/*
 * Polling AJAX cho khối "Trao đổi" (user-chi-tiet.html, admin-chi-tiet.html): gọi lại GET cùng đường dẫn
 * với form gửi tin (vd /user/5/binh-luan?sauMa=12) mỗi 4 giây, chỉ lấy tin có MA_BL lớn hơn tin mới nhất
 * đang có trên trang rồi nối thêm vào đầu danh sách — không tải lại cả trang khi có người trả lời.
 * Dừng polling khi tab không hiển thị (đổi tab khác) để đỡ tốn request.
 */
(function () {
  'use strict';

  var CHU_KY_MS = 4000;

  function htmlMotTin(bl) {
    var noi = bl.noiDung
      ? '<p class="noi-dung-bl">' + thoat(bl.noiDung).replace(/\n/g, '<br>') + '</p>'
      : '';
    var tep = '';
    if (bl.tepDinhKem) {
      var href = thoat(bl.tepDinhKem);
      tep = '<div class="tep-dinh-kem">' + (bl.laAnh
        ? '<a href="' + href + '" target="_blank"><img class="anh-to" src="' + href + '" alt="File đính kèm"></a>'
        : '<a href="' + href + '" target="_blank"><i class="bi bi-paperclip"></i> Xem file đính kèm</a>') + '</div>';
    }
    return '<div class="binh-luan">'
      + '<span class="avt' + (bl.laAdmin ? ' admin' : '') + '">' + thoat(bl.tenNguoiGui).charAt(0).toUpperCase() + '</span>'
      + '<div class="noi">'
      + '<div class="dau">'
      + '<span class="ten">' + thoat(bl.tenNguoiGui) + '</span>'
      + '<span class="badge ' + (bl.laAdmin ? 'text-bg-danger' : 'text-bg-secondary') + '">' + (bl.laAdmin ? 'IT' : 'Người báo') + '</span>'
      + '<span class="luc">' + thoat(bl.thoiGian) + '</span>'
      + '</div>' + noi + tep + '</div></div>';
  }

  function hoiVongMoi(khung) {
    var url = khung.getAttribute('data-url');
    var max = khung.getAttribute('data-max') || '0';
    fetch(url + (url.indexOf('?') === -1 ? '?' : '&') + 'sauMa=' + max)
      .then(function (r) { return r.ok ? r.json() : []; })
      .then(function (ds) {
        if (!ds || !ds.length) {
          return;
        }
        var trongRong = khung.querySelector('.trong-rong');
        if (trongRong) {
          trongRong.remove();
        }
        // ds đã sắp mới nhất lên trước (giống tầng repo) nên nối chuỗi theo đúng thứ tự rồi chèn vào đầu là đủ
        khung.insertAdjacentHTML('afterbegin', ds.map(htmlMotTin).join(''));
        khung.setAttribute('data-max', String(ds[0].maBl));
      })
      .catch(function () { /* mất mạng tạm thời: bỏ qua, chu kỳ sau thử lại */ });
  }

  var dsKhung = document.querySelectorAll('[data-binh-luan]');
  if (!dsKhung.length) {
    return;
  }
  var dangBat = true;
  document.addEventListener('visibilitychange', function () {
    dangBat = !document.hidden;
  });
  setInterval(function () {
    if (dangBat) {
      dsKhung.forEach(hoiVongMoi);
    }
  }, CHU_KY_MS);
})();

/*
 * Polling AJAX cho trang /admin (danh sách sự cố): tự hiện sự cố mới báo lên đầu bảng mà không cần F5.
 * Chỉ bật trên bảng có data-su-co-bat="true" - admin.html tự tính (đang xem "Tất cả"/"Mới", trang 1,
 * không có từ khóa tìm kiếm) để không làm sai lệch 1 trang đang lọc/phân trang khác.
 */
(function () {
  'use strict';

  var CHU_KY_MS = 5000;

  function htmlMotDong(sc) {
    var url = thoat('/admin/' + sc.maSc);
    return '<tr>'
      + '<td data-label="Mã" class="an-mobile">#' + sc.maSc + '</td>'
      + '<td class="o-chinh"><a href="' + url + '">' + thoat(sc.tieuDe) + '</a>'
      + '<span class="o-phu"><span class="d-md-none">#' + sc.maSc + ' · </span>'
      + '<i class="bi bi-person"></i> ' + thoat(sc.tenNguoiBao) + '</span></td>'
      + '<td data-label="Vị trí">' + thoat(sc.tenTb || '-')
      + (sc.tenPhong ? '<span class="o-phu">' + thoat(sc.tenPhong) + '</span>' : '') + '</td>'
      + '<td data-label="Danh mục">' + thoat(sc.tenDm) + '</td>'
      + '<td data-label="Ưu tiên"><span class="badge ' + sc.mucUuTienBadge + '">' + thoat(sc.mucUuTienNhan) + '</span></td>'
      + '<td data-label="Trạng thái"><span class="badge text-bg-secondary">Mới</span></td>'
      + '<td data-label="Ngày báo">' + thoat(sc.ngayTao) + '</td>'
      + '<td><span class="nhom-nut"><a class="btn btn-sm btn-outline-primary" href="' + url + '">'
      + '<i class="bi bi-wrench-adjustable"></i> Xử lý</a></span></td></tr>';
  }

  function capNhatDem(soLuong) {
    document.querySelectorAll('[data-dem="tong"], [data-dem="MOI"]').forEach(function (o) {
      o.textContent = String((parseInt(o.textContent, 10) || 0) + soLuong);
    });
  }

  function hoiVongMoi(bang) {
    var url = bang.getAttribute('data-su-co-url');
    var max = bang.getAttribute('data-su-co-max') || '0';
    fetch(url + (url.indexOf('?') === -1 ? '?' : '&') + 'sauMa=' + max)
      .then(function (r) { return r.ok ? r.json() : []; })
      .then(function (ds) {
        if (!ds || !ds.length) {
          return;
        }
        var than = bang.querySelector('tbody');
        var trongRong = than.querySelector('.trong-rong');
        if (trongRong) {
          trongRong.closest('tr').remove();
        }
        // ds đã sắp mới nhất lên trước (giống bảng) nên nối chuỗi theo đúng thứ tự rồi chèn vào đầu là đủ
        than.insertAdjacentHTML('afterbegin', ds.map(htmlMotDong).join(''));
        bang.setAttribute('data-su-co-max', String(ds[0].maSc));
        capNhatDem(ds.length);
      })
      .catch(function () { /* mất mạng tạm thời: bỏ qua, chu kỳ sau thử lại */ });
  }

  var dsBang = Array.prototype.filter.call(document.querySelectorAll('[data-su-co-url]'), function (b) {
    return b.getAttribute('data-su-co-bat') === 'true';
  });
  if (!dsBang.length) {
    return;
  }
  var dangBat = true;
  document.addEventListener('visibilitychange', function () {
    dangBat = !document.hidden;
  });
  setInterval(function () {
    if (dangBat) {
      dsBang.forEach(hoiVongMoi);
    }
  }, CHU_KY_MS);
})();
