package vn.utc.suco.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.DanhMuc;

@Repository
public class DanhMucRepo {
    private static final String SELECT = "SELECT MA_DM, TEN_DM, MO_TA FROM DANH_MUC_SU_CO ";

    private static final RowMapper<DanhMuc> MAPPER = (rs, i) -> new DanhMuc(
            rs.getLong("MA_DM"), rs.getString("TEN_DM"), rs.getString("MO_TA"));

    private final JdbcTemplate jdbc;

    public DanhMucRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<DanhMuc> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY TEN_DM", MAPPER);
    }

    public Optional<DanhMuc> timTheoMa(Long maDm) {
        return jdbc.query(SELECT + "WHERE MA_DM = ?", MAPPER, maDm).stream().findFirst();
    }

    /** Thêm danh mục. Tên trùng bị Oracle từ chối (ràng buộc UQ_DANH_MUC_TEN). */
    public void taoMoi(String tenDm, String moTa) {
        jdbc.update("INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES (?, ?)", tenDm, moTa);
    }

    public void capNhat(Long maDm, String tenDm, String moTa) {
        jdbc.update("UPDATE DANH_MUC_SU_CO SET TEN_DM = ?, MO_TA = ? WHERE MA_DM = ?", tenDm, moTa, maDm);
    }

    /** Xóa danh mục. Nếu còn sự cố hoặc bài kinh nghiệm tham chiếu, Oracle từ chối (ORA-02292). */
    public void xoa(Long maDm) {
        jdbc.update("DELETE FROM DANH_MUC_SU_CO WHERE MA_DM = ?", maDm);
    }
}
