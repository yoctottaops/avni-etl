package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;

public interface JdbcContextWrapper<T> {
    static <T> T runInOrgContext(JdbcContextWrapper<T> wrapper, JdbcTemplate jdbcTemplate) {
        return wrapper.wrap(jdbcTemplate);
    }

    T execute();

    default T wrap(JdbcTemplate jdbcTemplate) {
        String dbUser = ContextHolder.getDbUser();
        jdbcTemplate.execute("set role " + dbUser + ";");
        try {
            return execute();
        } finally {
            jdbcTemplate.execute("reset role;");
        }
    }
}
