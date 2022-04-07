package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.Column;

public class ConceptDatatypeMapper {
    public static Column.Type map(String type) {
        switch (type) {
            case "Numeric":
                return Column.Type.numeric;
            case "Duration":
            case "Date":
                return Column.Type.date;
            case "DateTime":
                return Column.Type.timestamp;
            case "Time":
                return Column.Type.time;
            default:
                return Column.Type.text;
        }
    }
}
