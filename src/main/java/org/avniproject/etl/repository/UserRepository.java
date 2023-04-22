package org.avniproject.etl.repository;

import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.User;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.repository.rowMappers.OrganisationIdentityRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        return new User();
    }

    public User findByUuid(String userUUID) {
        return new User();
    }
}
