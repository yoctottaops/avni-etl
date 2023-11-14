package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserActivityMapper implements RowMapper<UserActivityDTO> {
    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setId(rs.getLong("id"));
        userActivityDTO.setUserName(rs.getString("name"));
        userActivityDTO.setRegistrationCount(rs.getLong("registration_count"));
        userActivityDTO.setGeneralEncounterCount(rs.getLong("encounter_count"));
        userActivityDTO.setProgramEnrolmentCount(rs.getLong("enrolment_count"));
        userActivityDTO.setProgramEncounterCount(rs.getLong("program_encounter_count"));
        return userActivityDTO;
    }
}
