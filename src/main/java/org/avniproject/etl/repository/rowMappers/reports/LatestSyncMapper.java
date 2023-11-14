package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LatestSyncMapper implements RowMapper<UserActivityDTO> {

    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setUserName(rs.getString("name"));
        userActivityDTO.setAndroidVersion(rs.getString("android_version"));
        userActivityDTO.setAppVersion(rs.getString("app_version"));
        userActivityDTO.setDeviceModel(rs.getString("device_name"));
        userActivityDTO.setSyncStart(new DateTime(rs.getDate("sync_start_time")));
        userActivityDTO.setSyncEnd(new DateTime(rs.getDate("sync_end_time")));
        userActivityDTO.setSyncStatus(rs.getString("sync_status"));
        userActivityDTO.setSyncSource(rs.getString("sync_source"));
        return userActivityDTO;
    }
}
