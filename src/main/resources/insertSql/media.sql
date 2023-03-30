insert into ${schema_name}.${table_name} (entity_id, uuid, is_voided, created_by_id, last_modified_by_id, created_date_time,
                                  last_modified_date_time,
                                  address_id, image_url, sync_parameter_key1, sync_parameter_value1,
                                  sync_parameter_key2,
                                  sync_parameter_value2, subject_type_name, encounter_type_name, program_name,
                                  concept_name)
select id,
       uuid_generate_v4(),
       is_voided,
       created_by_id,
       last_modified_by_id,
       created_date_time,
       last_modified_date_time,
       address_id,
       ${conceptColumnName},
       ${syncRegistrationConcept1Name},
       ${syncRegistrationConcept1ColumnName},
       ${syncRegistrationConcept2Name},
       ${syncRegistrationConcept2ColumnName},
       ${subjectTypeName},
       ${encounterTypeName},
       ${programName},
       ${conceptName}
from ${schema_name}.${fromTableName} entity
where entity.last_modified_date_time > '${start_time}'
  and entity.last_modified_date_time <= '${end_time}';
