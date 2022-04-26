package org.avniproject.etl.repository.rowMappers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableNameGenerator {
    private static final int POSTGRES_MAX_TABLE_NAME_LENGTH = 63;

    private static final Map<String, List<Integer>> tableTypeTrimmingMap = new HashMap<String, List<Integer>>() {{
        put("Registration", Arrays.asList(6));
        put("Encounter", Arrays.asList(6, 20));
        put("ProgramEnrolment", Arrays.asList(6, 20));
        put("ProgramEncounter", Arrays.asList(6, 6, 20));
    }};

    private String buildProperTableName(List<String> entities) {
        List<String> list = entities.stream()
                .map(String::toLowerCase)
                .map(e -> e.replaceAll("[^a-z0-9_\\s]", "").replaceAll("\\s+", "_"))
                .collect(Collectors.toList());
        return String.join("_", list);
    }

    public String generateName(List<String> entities, String TableType, String suffix) {
        List<String> entitiesWithSuffix = new ArrayList<>(entities);
        if (suffix != null) {
            entitiesWithSuffix.add(suffix);
        }
        String TableName = buildProperTableName(entitiesWithSuffix);
        return TableName.length() > POSTGRES_MAX_TABLE_NAME_LENGTH ? getTrimmedTableName(entities, TableType, suffix) : TableName;
    }

    private String getTrimmedTableName(List<String> entities, String TableType, String suffix) {
        List<Integer> trimmingList = tableTypeTrimmingMap.get(TableType);
        List<String> trimmedNameList = IntStream
                .range(0, entities.size())
                .mapToObj(i -> getTrimmedName(entities, new StringBuilder(), trimmingList, i, suffix))
                .map(StringBuilder::toString)
                .collect(Collectors.toList());
        return buildProperTableName(trimmedNameList);
    }

    private StringBuilder getTrimmedName(List<String> entities, StringBuilder sb, List<Integer> trimmingList, int i, String suffix) {
        int lengthToConsider = trimmingList.get(i);
        String entityName = entities.get(i);
        if (lengthToConsider == 0) {
            sb.append(entityName);
        } else {
            String trimmedName = entityName.substring(0, Math.min(entityName.length(), lengthToConsider));
            sb.append(trimmedName);
        }
        if (suffix != null)
            sb.append(" ").append(suffix);
        return sb;
    }
}
