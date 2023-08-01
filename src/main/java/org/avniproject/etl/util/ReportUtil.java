package org.avniproject.etl.util;

import java.util.List;

import static java.lang.String.format;

public class ReportUtil {
    public String getDateDynamicWhere(String startDate, String endDate, String columnName) {
        if (startDate != null) {
            return format("and %s::date between '%s'::date and '%s'::date", columnName, startDate, endDate);
        }
        return "";
    }

    public String getDynamicUserWhere(List<Long> userIds, String columnName) {
        if (!userIds.isEmpty()) {
            return format("and %s in (%s)", columnName, StrUtil.joinLongToList(userIds));
        }
        return "";
    }

}
