insert into <schemaName>.<tableName> (entity_id, uuid, is_voided, created_by_id, last_modified_by_id, created_date_time,
                                  last_modified_date_time,organisation_id,
                                  address_id, image_url, sync_parameter_key1, sync_parameter_value1,
                                  sync_parameter_key2,
                                  sync_parameter_value2, subject_type_name, encounter_type_name, program_name,
                                  concept_name, subject_first_name, subject_last_name)
select entity.id,
       uuid_generate_v4(),
       entity.is_voided,
       entity.created_by_id,
       entity.last_modified_by_id,
       entity.created_date_time,
       entity.last_modified_date_time,
       entity.organisation_id,
       entity.address_id,
       entity.<conceptColumnName>,
       <if(syncRegistrationConcept1ColumnName)>
            <syncRegistrationConcept1Name>,
            entity.<syncRegistrationConcept1ColumnName>,
       <else>
            null, null,
       <endif>
       <if(syncRegistrationConcept2ColumnName)>
            <syncRegistrationConcept2Name>,
            entity.<syncRegistrationConcept2ColumnName>,
       <else>
            null, null,
       <endif>
       <subjectTypeName>,
       <encounterTypeName>,
       <programName>,
       <conceptName>,
       subject.first_name,
       subject.last_name
from <schemaName>.<fromTableName> entity
    inner join <schemaName>.<subjectTableName> subject on entity.<individualId> = subject.id
where entity.last_modified_date_time > '<startTime>'
  and entity.last_modified_date_time \<= '<endTime>';
