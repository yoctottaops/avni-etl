package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCountMapper implements RowMapper<UserActivityDTO> {
    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setUserName(rs.getString("name"));
        userActivityDTO.setCount(rs.getLong("count"));
        return userActivityDTO;
    }
}
