package org.avniproject.etl.util;

import java.util.List;
import java.util.stream.Collectors;

public class StrUtil {

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static String joinLongToList(List<Long> lists) {
        return lists.isEmpty() ? "" : lists.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
    }

}
