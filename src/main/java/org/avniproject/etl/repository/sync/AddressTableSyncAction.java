package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.dynamicInsert.TransactionalSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;
import static org.avniproject.etl.repository.dynamicInsert.SqlFile.readFile;

@Repository
public class AddressTableSyncAction implements EntitySyncAction {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddressTableSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean supports(TableMetadata tableMetadata) {
        return tableMetadata.getType().equals(TableMetadata.Type.Address);
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        if (!this.supports(tableMetadata)) {
            return;
        }
        insertLowestLevelAddresses(tableMetadata, lastSyncTime, dataSyncBoundaryTime);
    }

    private void insertLowestLevelAddresses(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        String templatePath = "/insertSql/address.sql";
        List<Map<String, Object>> lowestLevelsMap = runInOrgContext(() -> jdbcTemplate.queryForList("select name, id, parent_id from address_level_type where not is_voided;"), jdbcTemplate);
        lowestLevelsMap.forEach(lowestLevel -> {
            String levelName = (String) lowestLevel.get("name");
            String sql = new TransactionalSyncSqlGenerator().getSql(templatePath, tableMetadata, lastSyncTime, dataSyncBoundaryTime);
            String query = sql.replace("${titleColumnName}", levelName)
                    .replace("${idColumnName}", format("%s id", levelName));
            runAddressQuery(query);
            insertParents((Integer) lowestLevel.get("id"), levelName, (Integer) lowestLevel.get("parent_id"));
        });
    }

    private void insertParents(Integer childLevelId, String childLevelName, Integer parentLevelId) {
        List<Map<String, Object>> parentLevelsMap = runInOrgContext(() -> jdbcTemplate.queryForList(format("select name, id, parent_id from address_level_type where id = %d;", parentLevelId)), jdbcTemplate);
        if (parentLevelId == null) return;
        parentLevelsMap.forEach(parentLevelMap -> {
            String parentLevelName = (String) parentLevelMap.get("name");
            String levelName = (String) parentLevelMap.get("name");
            String query = format("update \"%s\".address a\n" +
                    "set \"%s\" = al.title,\n" +
                    "\"%s id\" = al.id\n" +
                    "from address_level al\n" +
                    "join address_level cal on cal.parent_id = al.id\n" +
                    "join address_level_type calt on calt.id = cal.type_id\n" +
                    "where calt.id = %d\n" +
                    "and cal.id = a.\"%s id\"", ContextHolder.getDbSchema(), parentLevelName, parentLevelName, childLevelId, childLevelName);
            runAddressQuery(query);
            insertParents((Integer) parentLevelMap.get("id"), levelName, (Integer) parentLevelMap.get("parent_id"));
        });
    }

    private void runAddressQuery(String query) {
        runInOrgContext(() -> {
            jdbcTemplate.execute(query);
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
