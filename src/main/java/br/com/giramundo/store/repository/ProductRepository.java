package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbc;

    public ProductRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Product> mapper = new RowMapper<>() {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product p = new Product();
            p.setId(rs.getString("id"));
            p.setName(rs.getString("name"));
            p.setDescription(rs.getString("description"));
            p.setPrice(rs.getDouble("price"));
            p.setSku(rs.getString("sku"));
            p.setImage(rs.getString("image"));
            return p;
        }
    };

    public List<Product> findAll() {
        return jdbc.query("SELECT id, name, description, price, sku, image FROM product ORDER BY id", mapper);
    }

    public Product findById(String id) {
        return jdbc.queryForObject("SELECT id, name, description, price, sku, image FROM product WHERE id = ?", new Object[]{id}, mapper);
    }

    public int save(Product p) {
        if (p.getId() == null || p.getId().isEmpty()) {
            String id = UUID.randomUUID().toString();
            p.setId(id);
            return jdbc.update("INSERT INTO product(id, name, description, price, sku, image) VALUES(?,?,?,?,?,?)",
                    p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getSku(), p.getImage());
        } else {
            return jdbc.update("UPDATE product SET name = ?, description = ?, price = ?, sku = ?, image = ? WHERE id = ?",
                    p.getName(), p.getDescription(), p.getPrice(), p.getSku(), p.getImage(), p.getId());
        }
    }

    public int deleteById(String id) {
        return jdbc.update("DELETE FROM product WHERE id = ?", id);
    }
}
