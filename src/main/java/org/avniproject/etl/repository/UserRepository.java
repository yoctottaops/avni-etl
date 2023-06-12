package org.avniproject.etl.repository;

import org.avniproject.etl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.String.format;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private User user;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = format("SELECT * FROM users WHERE username = '%s'", username);

        jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
        return this.user;
    }

    public User findByUuid(String userUUID) {
        String sql = format("SELECT * FROM users WHERE uuid = '%s'", userUUID);

        jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
        return this.user;
    }

    private Object setUser(ResultSet rs) {
        try {
            this.user = new User(rs.getString("username"), rs.getString("uuid"), rs.getLong("organisation_id"));
            return  rs;
        } catch (SQLException e) {
            System.out.println("Exception occurred--" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
