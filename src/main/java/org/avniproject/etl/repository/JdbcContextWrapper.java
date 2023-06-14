package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.service.BaseIAMService;
import org.avniproject.etl.util.InterfaceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public interface JdbcContextWrapper<T> {
    static <T> T runInOrgContext(JdbcContextWrapper<T> wrapper, JdbcTemplate jdbcTemplate) {
        return wrapper.wrapInOrgContext(jdbcTemplate);
    }

    static <T> T runInSchemaUserContext(JdbcContextWrapper<T> wrapper, JdbcTemplate jdbcTemplate) {
        return wrapper.wrapInSchemaContext(jdbcTemplate);
    }

    T execute();

    default T wrapInOrgContext(JdbcTemplate jdbcTemplate) {
        String dbUser = ContextHolder.getDbUser();
        return wrap(jdbcTemplate, dbUser);
    }

    default T wrapInSchemaContext(JdbcTemplate jdbcTemplate) {
        String dbUser = ContextHolder.getSchemaUser();
        return wrap(jdbcTemplate, dbUser);
    }

    private T wrap(JdbcTemplate jdbcTemplate, String dbUser) {
        jdbcTemplate.execute("set role " + dbUser + ";");
        InterfaceLogger.JdbcContextWrapper.debug(String.format("[%s] Executing with dbUser: %s", ContextHolder.getDbSchema(), dbUser));
        try {
            return execute();
        } finally {
            jdbcTemplate.execute("reset role;");
        }
    }
}
