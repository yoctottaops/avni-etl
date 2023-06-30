--[SQL template for auto generated view]
insert into ${schema_name}.${table_name} (
    "individual_id", "encounter_id", "address_id", "is_voided", "organisation_id", "last_modified_date_time"
    ${observations_to_insert_list}
)
(${concept_maps}
SELECT entity.individual_id                                                                "individual_id",
       entity.encounter_id                                                                 "encounter_id",
       entity.address_id                                                                   "address_id",
       entity.is_voided                                                                    "is_voided",
       entity.organisation_id                                                              "organisation_id",
       entity.last_modified_date_time                                                      "last_modified_date_time"
       ${selections}
FROM (
    select jsonb_array_elements((mainTable.observations ->> '${repeatable_question_group_concept_uuid}')::jsonb)  as observations,
    individual_id                                                                                     as individual_id,
    mainTable.id                                                                                     as encounter_id,
    mainTable.address_id                                                                             as address_id,
    mainTable.is_voided                                                                              as is_voided,
    mainTable.organisation_id                                                                        as organisation_id,
    mainTable.last_modified_date_time                                                                as last_modified_date_time,
    mainTable.encounter_type_id                                                                      as encounter_type_id,
    mainTable.cancel_date_time                                                                      as cancel_date_time
    from public.encounter mainTable
    inner join public.individual ind on mainTable.individual_id = ind.id
    inner join public.encounter_type et on mainTable.encounter_type_id = et.id
    inner join public.subject_type st on st.id = ind.subject_type_id
    where et.uuid = '${encounter_type_uuid}'
    and mainTable.observations ->> '${repeatable_question_group_concept_uuid}' is not null
    and jsonb_array_length((mainTable.observations ->> '${repeatable_question_group_concept_uuid}')::jsonb) > 0
    ) entity
LEFT OUTER JOIN public.individual ind on entity.individual_id = ind.id
LEFT OUTER JOIN public.encounter_type et on entity.encounter_type_id = et.id
LEFT OUTER JOIN public.subject_type st on st.id = ind.subject_type_id
${cross_join_concept_maps}
WHERE st.uuid = '${subject_type_uuid}'
  AND et.uuid = '${encounter_type_uuid}'
  AND entity.cancel_date_time isnull
    and entity.last_modified_date_time > '${start_time}'
    and entity.last_modified_date_time <= '${end_time}'
    );
