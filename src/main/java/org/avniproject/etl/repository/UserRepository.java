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
    private final String FIND_BY_UUID = "select u.id,\n" +
            "       u.username username,\n" +
            "       u.uuid uuid,\n" +
            "       u.organisation_id organisation_id,\n" +
            "       case when count(p.id) > 0 or bool_or(g.has_all_privileges) = true then true else false end as has_analytics_access,\n" +
            "       case when count(aa.id) > 0 then true else false end                                        as is_admin\n" +
            "from users u\n" +
            "         left join account_admin aa on u.id = aa.admin_id\n" +
            "         left join user_group ug on u.id = ug.user_id and ug.is_voided is false\n" +
            "         left join groups g on ug.group_id = g.id\n" +
            "         left join group_privilege gp on ug.id = gp.group_id and gp.is_voided is false\n" +
            "         left join privilege p on gp.privilege_id = p.id and p.name = 'Analytics'\n" +
            "where u.uuid = '%s'\n" +
            "group by u.id, u.username, u.uuid, u.organisation_id;";

    private final String FIND_BY_USERNAME = "select u.id,\n" +
            "       u.username username,\n" +
            "       u.uuid uuid,\n" +
            "       u.organisation_id organisation_id,\n" +
            "       case when count(p.id) > 0 or bool_or(g.has_all_privileges) = true then true else false end as has_analytics_access,\n" +
            "       case when count(aa.id) > 0 then true else false end                                        as is_admin\n" +
            "from users u\n" +
            "         left join account_admin aa on u.id = aa.admin_id\n" +
            "         left join user_group ug on u.id = ug.user_id and ug.is_voided is false\n" +
            "         left join groups g on ug.group_id = g.id\n" +
            "         left join group_privilege gp on ug.id = gp.group_id and gp.is_voided is false\n" +
            "         left join privilege p on gp.privilege_id = p.id and p.name = 'Analytics'\n" +
            "where u.username = '%s'\n" +
            "group by u.id, u.username, u.uuid, u.organisation_id;";

    private User user;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = format(FIND_BY_USERNAME, username);
        try {
            jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
        }
        catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
        return this.user;
    }

    public User findByUuid(String userUUID) {
        String sql = format(FIND_BY_UUID, userUUID);

        jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setUser(rs));
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
