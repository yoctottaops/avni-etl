package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SummaryTableMapper implements RowMapper<UserActivityDTO> {
    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setTableName(rs.getString("name"));
        userActivityDTO.setTableType(rs.getString("type"));
        return userActivityDTO;
    }
}
