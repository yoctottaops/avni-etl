package org.avniproject.etl.repository;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.util.InterfaceLogger;
import org.springframework.jdbc.UncategorizedSQLException;
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
        String dbUser = OrgIdentityContextHolder.getDbUser();
        return wrap(jdbcTemplate, dbUser);
    }

    default T wrapInSchemaContext(JdbcTemplate jdbcTemplate) {
        String dbUser = OrgIdentityContextHolder.getSchemaUser();
        return wrap(jdbcTemplate, dbUser);
    }

    private T wrap(JdbcTemplate jdbcTemplate, String dbUser) {
        jdbcTemplate.execute("set role \"" + dbUser + "\";");
        InterfaceLogger.JdbcContextWrapper.debug(String.format("[%s] Executing with dbUser: %s", OrgIdentityContextHolder.getDbSchema(), dbUser));
        try {
            return execute();
        } catch (UncategorizedSQLException uncategorizedSQLException) {
            InterfaceLogger.JdbcContextWrapper.error("Execution failed for SQL: " + uncategorizedSQLException.getSql());
            throw uncategorizedSQLException;
        }
        finally {
            try {
                jdbcTemplate.execute("reset role;");
            } catch (UncategorizedSQLException uncategorizedSQLException) {
                if (!uncategorizedSQLException.getSQLException().getSQLState().equals("25P02"))
                    throw uncategorizedSQLException;
            }
        }
    }
}
