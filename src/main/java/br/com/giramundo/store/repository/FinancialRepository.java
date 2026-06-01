package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.FinancialEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class FinancialRepository {

    private final JdbcTemplate jdbc;

    public FinancialRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<FinancialEntry> mapper = new RowMapper<>() {
        @Override
        public FinancialEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            FinancialEntry f = new FinancialEntry();
            f.setId(rs.getString("id"));
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

    public FinancialEntry findById(String id) {
        return jdbc.queryForObject("SELECT id, type, amount, description, occurred_at FROM financial_entry WHERE id = ?", new Object[]{id}, mapper);
    }

    public int save(FinancialEntry f) {
        if (f.getId() == null || f.getId().isEmpty()) {
            String id = UUID.randomUUID().toString();
            f.setId(id);
            return jdbc.update("INSERT INTO financial_entry(id, type, amount, description, occurred_at) VALUES(?,?,?,?,?)",
                    f.getId(), f.getType(), f.getAmount(), f.getDescription(), f.getOccurredAt());
        } else {
            return jdbc.update("UPDATE financial_entry SET type = ?, amount = ?, description = ?, occurred_at = ? WHERE id = ?",
                    f.getType(), f.getAmount(), f.getDescription(), f.getOccurredAt(), f.getId());
        }
    }

    public int deleteById(String id) {
        return jdbc.update("DELETE FROM financial_entry WHERE id = ?", id);
    }
}
