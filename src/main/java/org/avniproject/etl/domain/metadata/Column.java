package org.avniproject.etl.domain.metadata;

public class Column {
    public enum Type {
        text,
        date,
        bool,
        timestamp,
        point,
        timestampWithTimezone,
        integer;

        public String typeString() {
            switch (this) {
                case integer: return "integer";
                case date: return "date";
                case text: return "text";
                case bool: return "boolean";
                case point: return "point";
                case timestamp: return "timestamp";
                case timestampWithTimezone: return "timestamp with time zone";
                default: throw new RuntimeException("column_name is not defined for this type"); //Not an expected scenario
            }
        }
    }

    private String name;
    private Type type;

    public Column(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
