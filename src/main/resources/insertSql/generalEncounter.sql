--[SQL template for auto generated view]
insert into ${schema_name}.${table_name} (
    "individual_id", "id", "earliest_visit_date_time", "encounter_date_time", "uuid", "name", "max_visit_date_time", "is_voided",
    "encounter_location", "cancel_date_time", "cancel_location", "created_by_id", "last_modified_by_id",
    "created_date_time", "last_modified_date_time", "legacy_id"
    ${observations_to_insert_list}
)
(${concept_maps}
SELECT entity.individual_id                                                                "individual_id",
       entity.id                                                                           "id",
       entity.earliest_visit_date_time                                                     "earliest_visit_date_time",
       entity.encounter_date_time                                                          "encounter_date_time",
       entity.uuid                                                                         "uuid",
       entity.name                                                                         "name",
       entity.max_visit_date_time                                                          "max_visit_date_time",
       entity.is_voided                                                                    "is_voided",
       entity.encounter_location                                                           "encounter_location",
       entity.cancel_date_time                                                             "cancel_date_time",
       entity.cancel_location                                                              "cancel_location",
       entity.created_by_id                                                                "created_by_id",
       entity.last_modified_by_id                                                          "last_modified_by_id",
       entity.created_date_time                                                            "created_date_time",
       entity.last_modified_date_time                                                      "last_modified_date_time",
       entity.legacy_id                                                                    "legacy_id"
       ${selections}
FROM public.encounter entity
    inner join individual subject on entity.individual_id = subject.id
  ${cross_join_concept_maps}
WHERE entity.encounter_type_id = ${encounter_type_id}
  AND subject.subject_type_id = ${subject_type_id}
    and entity.last_modified_date_time > '${start_time}'
    and entity.last_modified_date_time <= '${end_time}'
    );
