package br.com.giramundo.store.config;

import java.util.Arrays;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.H2Dialect;
import org.springframework.data.jdbc.repository.config.DialectResolver.NoDialectException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@Configuration
public class JdbcDialectConfig {

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new UuidToStringConverter(),
                new StringToUuidConverter()));
    }

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

    @WritingConverter
    static class UuidToStringConverter implements Converter<UUID, String> {
        @Override
        public String convert(UUID source) {
            return source == null ? null : source.toString();
        }
    }

    @ReadingConverter
    static class StringToUuidConverter implements Converter<String, UUID> {
        @Override
        public UUID convert(String source) {
            return source == null || source.isBlank() ? null : UUID.fromString(source);
        }
    }

}
