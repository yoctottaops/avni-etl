--[SQL template for auto generated view]
insert into ${schema_name}.${table_name} (
    "individual_id", "id", "earliest_visit_date_time", "encounter_date_time", "uuid", "name", "max_visit_date_time", "is_voided",
    "encounter_location", "audit_id", "cancel_date_time", "cancel_location", "created_by_id", "last_modified_by_id",
    "created_date_time", "last_modified_date_time", "legacy_id"
    ${observations_to_insert_list}
)
(${concept_maps}
SELECT encounter.individual_id                                                                "individual_id",
       encounter.address_id                                                                   "address_id",
       encounter.id                                                                           "id",
       encounter.earliest_visit_date_time                                                     "earliest_visit_date_time",
       encounter.encounter_date_time                                                          "encounter_date_time",
       encounter.uuid                                                                         "uuid",
       encounter.name                                                                         "name",
       encounter.max_visit_date_time                                                          "max_visit_date_time",
       encounter.is_voided                                                                    "is_voided",
       encounter.encounter_location                                                           "encounter_location",
       encounter.audit_id                                                                     "audit_id",
       encounter.cancel_date_time                                                             "cancel_date_time",
       encounter.cancel_location                                                              "cancel_location",
       encounter.created_by_id                                                                "created_by_id",
       encounter.last_modified_by_id                                                          "last_modified_by_id",
       encounter.created_date_time                                                            "created_date_time",
       encounter.last_modified_date_time                                                      "last_modified_date_time",
       encounter.legacy_id                                                                    "legacy_id"
       ${selections}
FROM public.encounter encounter
    inner join individual subject on encounter.individual_id = individual.id
  ${cross_join_concept_maps}
WHERE encounter.encounter_type_id = '${encounter_type_id}'
  AND individual.subject_type_id = '${subject_type_id}'
    and encounter.last_modified_date_time > '${start_time}'
    and encounter.last_modified_date_time <= '${end_time}'
    );
