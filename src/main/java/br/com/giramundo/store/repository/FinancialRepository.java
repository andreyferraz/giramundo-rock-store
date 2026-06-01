package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.FinancialEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FinancialRepository {

    private final JdbcTemplate jdbc;

    public FinancialRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<FinancialEntry> mapper = new RowMapper<>() {
        @Override
        public FinancialEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            FinancialEntry f = new FinancialEntry();
            f.setId(rs.getLong("id"));
            f.setType(rs.getString("type"));
            f.setAmount(rs.getDouble("amount"));
            f.setDescription(rs.getString("description"));
            f.setOccurredAt(rs.getString("occurred_at"));
            return f;
        }
    };

    public List<FinancialEntry> findAll() {
        return jdbc.query("SELECT id, type, amount, description, occurred_at FROM financial_entry ORDER BY occurred_at DESC", mapper);
    }

    public FinancialEntry findById(Long id) {
        return jdbc.queryForObject("SELECT id, type, amount, description, occurred_at FROM financial_entry WHERE id = ?", new Object[]{id}, mapper);
    }

    public int save(FinancialEntry f) {
        if (f.getId() == null) {
            return jdbc.update("INSERT INTO financial_entry(type, amount, description, occurred_at) VALUES(?,?,?,?)",
                    f.getType(), f.getAmount(), f.getDescription(), f.getOccurredAt());
        } else {
            return jdbc.update("UPDATE financial_entry SET type = ?, amount = ?, description = ?, occurred_at = ? WHERE id = ?",
                    f.getType(), f.getAmount(), f.getDescription(), f.getOccurredAt(), f.getId());
        }
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM financial_entry WHERE id = ?", id);
    }
}
