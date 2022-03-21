package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.Column;

public class ConceptDatatypeMapper {
    public static Column.Type map(String type) {
        switch (type) {
            case "Numeric":
                return Column.Type.integer;
            case "Duration":
            case "Date":
                return Column.Type.date;
            case "DateTime":
            case "Time":
                return Column.Type.timestamp;
            case "Location":
                return Column.Type.point;
            default:
                return Column.Type.text;
        }
    }
}
