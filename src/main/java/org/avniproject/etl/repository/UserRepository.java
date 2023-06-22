package org.avniproject.etl.repository;

import org.apache.log4j.Logger;
import org.avniproject.etl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.String.format;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private User user;
    private static final Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = format("SELECT * FROM users WHERE username = '%s'", username);
        try {
            jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
        }
        catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
        return this.user;
    }

    public User findByUuid(String userUUID) {
        String sql = format("SELECT * FROM users WHERE uuid = '%s'", userUUID);

        jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
        return this.user;
    }

    private Object setUser(ResultSet rs) {
        try {
            this.user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("uuid"), rs.getLong("organisation_id"), rs.getBoolean("is_org_admin"));
            return  rs;
        } catch (SQLException e) {
            logger.debug("Exception occurred--" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
