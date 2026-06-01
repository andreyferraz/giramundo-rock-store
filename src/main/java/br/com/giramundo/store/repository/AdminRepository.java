package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Admin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbc;

    public AdminRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Admin> mapper = new RowMapper<>() {
        @Override
        public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
            Admin a = new Admin();
            a.setId(rs.getLong("id"));
            a.setUsername(rs.getString("username"));
            a.setPassword(rs.getString("password"));
            return a;
        }
    };

    public List<Admin> findAll() {
        return jdbc.query("SELECT id, username, password FROM admin ORDER BY id", mapper);
    }

    public Admin findById(Long id) {
        return jdbc.queryForObject("SELECT id, username, password FROM admin WHERE id = ?", new Object[]{id}, mapper);
    }

    public Admin findByUsername(String username) {
        List<Admin> list = jdbc.query("SELECT id, username, password FROM admin WHERE username = ?", new Object[]{username}, mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Admin admin) {
        if (admin.getId() == null) {
            return jdbc.update("INSERT INTO admin(username, password) VALUES(?, ?)", admin.getUsername(), admin.getPassword());
        } else {
            return jdbc.update("UPDATE admin SET username = ?, password = ? WHERE id = ?", admin.getUsername(), admin.getPassword(), admin.getId());
        }
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM admin WHERE id = ?", id);
    }
}
