package org.avniproject.etl.repository.rowMappers.reports;

import org.avniproject.etl.dto.UserActivityDTO;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MedianSyncMapper implements RowMapper<UserActivityDTO> {
    @Override
    public UserActivityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserActivityDTO userActivityDTO = new UserActivityDTO();
        userActivityDTO.setSyncStart(new DateTime(rs.getDate("start_date")));
        userActivityDTO.setSyncEnd(new DateTime(rs.getDate("end_date")));
        userActivityDTO.setMedianSync(rs.getString("median_sync_time"));
        return userActivityDTO;
    }
}
