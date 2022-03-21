package org.avniproject.etl.repository.rowMappers.tableMappers;

import java.util.Map;

public class EncounterCancellationTable extends EncounterTable {
    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("Encounter", " CANCEL", tableDetails, "subject_type_name", "encounter_type_name");
    }
}
