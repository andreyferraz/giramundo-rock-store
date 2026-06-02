package br.com.giramundo.store.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.H2Dialect;
import org.springframework.data.jdbc.repository.config.DialectResolver.NoDialectException;

@Configuration
public class JdbcDialectConfig {

    @Bean
    public Dialect jdbcDialect(DataSource dataSource) {
        try {
            return DialectResolver.getDialect(new JdbcTemplate(dataSource));
        } catch (NoDialectException ex) {
            // Fallback to H2 dialect for development when the driver/dialect cannot
            // be automatically resolved (e.g., SQLite). H2 is fairly compatible for
            // simple SQL used by Spring Data JDBC in this project.
            return H2Dialect.INSTANCE;
        }
    }

}
