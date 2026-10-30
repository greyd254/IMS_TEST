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
