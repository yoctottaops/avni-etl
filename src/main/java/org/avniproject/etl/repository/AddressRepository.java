package org.avniproject.etl.repository;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

import static org.avniproject.etl.repository.sql.SqlFile.readFile;

@Repository
public class AddressRepository {
    private JdbcTemplate jdbcTemplate;
    private static final String sql = readFile("/sql/api/ensureAddressColumnExists.sql.st");

    @Autowired
    public AddressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean doAllAddressLevelTypeNamesExist(List<String> addressLevelTypeNames) {
        if (addressLevelTypeNames == null || addressLevelTypeNames.isEmpty()) {
            return true;
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("schemaName", OrgIdentityContextHolder.getDbSchema());
        parameters.put("addressLevelTypeNames", addressLevelTypeNames);

        Integer numberOfAddressLevelTypes = new NamedParameterJdbcTemplate(jdbcTemplate).queryForObject(sql, parameters, Integer.class);
        return numberOfAddressLevelTypes == addressLevelTypeNames.size();
    }
}
