package org.avniproject.etl.repository.rowMappers.tableMappers;

import java.util.Map;

public class ProgramEncounterCancellationTable extends ProgramEncounterTable {

    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("ProgramEncounter", "CANCEL", tableDetails, "subject_type_name", "program_name", "encounter_type_name");
    }
}
