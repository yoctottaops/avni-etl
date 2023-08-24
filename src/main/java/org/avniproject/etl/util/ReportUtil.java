package org.avniproject.etl.util;

import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
public class ReportUtil {
    public String getDateDynamicWhere(String startDate, String endDate, String columnName) {
        if (startDate != null) {
            return format("and %s::date between '%s'::date and '%s'::date", columnName, startDate, endDate);
        }
        return "";
    }

    public String getDateDynamicMedianSync(String startDate, String endDate, String columnName) {
        if (startDate != null) {
            return format("and %s::date between ( '%s'::date - interval '7 days') and ('%s'::date - interval '7 days')", columnName, startDate, endDate);
        }
        return "and st.sync_start_time between ( current_date at time zone 'UTC'+'5:30' - interval '7 days') and current_date";
    }

    public String getDynamicUserWhere(List<Long> userIds, String columnName) {
        if (!userIds.isEmpty()) {
            return format("and %s in (%s)", columnName, StrUtil.joinLongToList(userIds));
        }
        return "";
    }

}
