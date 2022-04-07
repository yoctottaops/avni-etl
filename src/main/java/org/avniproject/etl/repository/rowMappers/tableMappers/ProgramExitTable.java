package org.avniproject.etl.repository.rowMappers.tableMappers;


import java.util.Map;

public class ProgramExitTable extends ProgramEnrolmentTable {
    @Override
    public String name(Map<String, Object> tableDetails) {
        return generateTableName("ProgramEnrolment", "EXIT", tableDetails, "subject_type_name", "program_name");
    }
}
