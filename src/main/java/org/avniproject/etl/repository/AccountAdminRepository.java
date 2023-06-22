package org.avniproject.etl.repository;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Repository
public class AccountAdminRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(AccountAdminRepository.class);

    @Autowired
    public AccountAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findByUserId(Long userId) {
        String sql = format("SELECT * FROM account_admin WHERE id = %d", userId);

        return jdbcTemplate.queryForList(sql);
    }
}
