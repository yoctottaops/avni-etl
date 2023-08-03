package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailsMapper implements RowMapper<UserActivityDTO> {
    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setUserName(rs.getString("name"));
        userActivityDTO.setAppVersion(rs.getString("app_version"));
        userActivityDTO.setDeviceModel(rs.getString("device_model"));
        userActivityDTO.setLastSuccessfulSync(new DateTime(rs.getDate("sync_start_time")));
        return userActivityDTO;
    }
}
