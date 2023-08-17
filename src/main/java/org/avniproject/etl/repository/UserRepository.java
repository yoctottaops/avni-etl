package org.avniproject.etl.repository;

import org.apache.log4j.Logger;
import org.avniproject.etl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.avniproject.etl.repository.sql.SqlFile.readFile;

@Repository
public class UserRepository {
    private final String FIND_BY_UUID = readFile("sql/api/findByUuid.sql");
    private final String FIND_BY_USERNAME = readFile("sql/api/findByUsername.sql");

    private User user;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        try {
            Map<String, Object> parameters = new HashMap<>(1);
            parameters.put("username", username);
            new NamedParameterJdbcTemplate(jdbcTemplate).queryForObject(FIND_BY_USERNAME, parameters, (rs, rowNum) -> setUser(rs));
        }
        catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
        return this.user;
    }

    public User findByUuid(String userUUID) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("uuid", userUUID);

        new NamedParameterJdbcTemplate(jdbcTemplate).queryForObject(FIND_BY_UUID, parameters, (rs, rowNum) -> setUser(rs));

        return this.user;
    }

    private Object setUser(ResultSet rs) {
        try {
            this.user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("uuid"), rs.getLong("organisation_id"), rs.getBoolean("has_analytics_access"), rs.getBoolean("is_admin"));
            return  rs;
        } catch (SQLException e) {
            logger.debug("Exception occurred--" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
