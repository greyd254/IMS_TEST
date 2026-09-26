package vn.utc.suco.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.DanhMuc;

@Repository
public class DanhMucRepo {
    private final JdbcTemplate jdbc;

    public DanhMucRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<DanhMuc> timTatCa() {
        return jdbc.query("SELECT MA_DM, TEN_DM FROM DANH_MUC_SU_CO ORDER BY TEN_DM",
                (rs, i) -> new DanhMuc(rs.getLong("MA_DM"), rs.getString("TEN_DM")));
    }
}
