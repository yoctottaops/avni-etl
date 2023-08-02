package org.avniproject.etl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { EtlApplication.class },
        initializers = ConfigDataApplicationContextInitializer.class)
public class BaseIntegrationTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected Long countOfRowsIn(String tableName) {
        List<Map<String, Object>> countResult = jdbcTemplate.queryForList(String.format("select count(*) as cnt from %s", tableName));
        return (Long) countResult.get(0).get("cnt");
    }
}
