package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.TableMetadata;
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
        String template = readFile("/insertSql/address.sql");
        insertLowestLevelAddresses(lastSyncTime, dataSyncBoundaryTime, template);
    }

    private void insertLowestLevelAddresses(Date lastSyncTime, Date dataSyncBoundaryTime, String template) {
        List<Map<String, Object>> lowestLevelsMap = runInOrgContext(() -> jdbcTemplate.queryForList("select name, id, parent_id from address_level_type where not is_voided;"), jdbcTemplate);
        lowestLevelsMap.forEach(lowestLevel -> {
            String levelName = (String) lowestLevel.get("name");
            String query = template.replace("${schema_name}", format("\"%s\"", ContextHolder.getDbSchema()))
                    .replace("${titleColumnName}", levelName)
                    .replace("${idColumnName}", format("%s id", levelName))
                    .replace("${start_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(lastSyncTime))
                    .replace("${end_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(dataSyncBoundaryTime));
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
