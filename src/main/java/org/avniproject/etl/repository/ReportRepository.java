package org.avniproject.etl.repository;

import org.avniproject.etl.dto.UserActivityDTO;
import org.avniproject.etl.repository.rowMappers.reports.UserActivityMapper;
import org.avniproject.etl.repository.rowMappers.reports.UserCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserActivityDTO> getUserActivity(String orgSchemaName, String subjectWhere, String encounterWhere, String enrolmentWhere, String userWhere) {
        String baseQuery = "with registrations as (\n" +
                "    select last_modified_by_id, count(*) as registration_count\n" +
                "    from ${schemaName}.individual\n" +
                "    where is_voided = false\n" +
                "    ${subjectWhere}\n" +
                "    group by last_modified_by_id\n" +
                "),\n" +
                "     encounters as (\n" +
                "         select last_modified_by_id, count(*) as encounter_count\n" +
                "         from ${schemaName}.encounter\n" +
                "         where is_voided = false\n" +
                "         ${encounterWhere}\n" +
                "         group by last_modified_by_id\n" +
                "     ),\n" +
                "     enrolments as (\n" +
                "         select last_modified_by_id, count(*) as enrolment_count\n" +
                "         from ${schemaName}.program_enrolment\n" +
                "         where is_voided = false\n" +
                "         ${enrolmentWhere}\n" +
                "         group by last_modified_by_id\n" +
                "     ),\n" +
                "     program_encounters as (\n" +
                "         select last_modified_by_id, count(*) as program_encounter_count\n" +
                "         from ${schemaName}.program_encounter\n" +
                "         where is_voided = false\n" +
                "         ${encounterWhere}\n" +
                "         group by last_modified_by_id\n" +
                "     )\n" +
                "select u.id                                              as id,\n" +
                "       coalesce(u.name, u.username)                      as name,\n" +
                "       coalesce(registration_count, 0)                   as registration_count,\n" +
                "       coalesce(encounter_count, 0)                      as encounter_count,\n" +
                "       coalesce(enrolment_count, 0)                      as enrolment_count,\n" +
                "       coalesce(program_encounter_count, 0)              as program_encounter_count,\n" +
                "       coalesce(coalesce(registration_count, 0) + coalesce(encounter_count, 0) + coalesce(enrolment_count, 0) +\n" +
                "                coalesce(program_encounter_count, 0), 0) as total\n" +
                "from ${schemaName}.users u\n" +
                "         left join ${schemaName}.registrations r on r.last_modified_by_id = u.id\n" +
                "         left join ${schemaName}.encounters e on e.last_modified_by_id = u.id\n" +
                "         left join ${schemaName}.enrolments enl on enl.last_modified_by_id = u.id\n" +
                "         left join ${schemaName}.program_encounters enc on enc.last_modified_by_id = u.id\n" +
                "where u.is_voided = false and u.organisation_id notnull\n" +
                "       and coalesce(coalesce(registration_count, 0) + coalesce(encounter_count, 0) + coalesce(enrolment_count, 0) +\n" +
                "                coalesce(program_encounter_count, 0), 0) > 0\n" +
                "      ${userWhere}\n" +
                "order by 7 desc\n" +
                "limit 10;";
        String query = baseQuery
                .replace("${schemaName}", orgSchemaName)
                .replace("${subjectWhere}", subjectWhere)
                .replace("${encounterWhere}", encounterWhere)
                .replace("${enrolmentWhere}", enrolmentWhere)
                .replace("${userWhere}", userWhere);
        return jdbcTemplate.query(query, new UserActivityMapper());
    }

    public List<UserActivityDTO> generateUserSyncFailures(String orgSchemaName, String syncTelemetryWhere, String userWhere) {
        String baseQuery = "select coalesce(u.name, u.username) as name, \n" +
                "       count(*) as count\n" +
                "from ${schemaName}.sync_telemetry st\n" +
                "         join ${schemaName}.user u on st.user_id = u.id\n" +
                "where sync_status = 'incomplete'\n" +
                "and (u.is_voided = false or u.is_voided isnull)\n" +
                "and u.organisation_id notnull\n" +
                "${syncTelemetryWhere}\n"+
                "${userWhere}\n"+
                "group by 1\n" +
                "order by 2 desc\n" +
                "limit 10;";
        String query = baseQuery
                .replace("${schemaName}", orgSchemaName)
                .replace("${syncTelemetryWhere}", syncTelemetryWhere)
                .replace("${userWhere}", userWhere);
        return jdbcTemplate.query(query, new UserCountMapper());
    }
}
