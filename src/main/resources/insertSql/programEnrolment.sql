--[SQL template for auto generated view]
insert into ${schema_name}.${table_name} (
    "id",
    "individual_id",
    "uuid",
    "address_id",
    "enrolment_date_time",
    "enrolment_location",
    "is_voided",
    "program_exit_date_time",
    "exit_location",
    "created_by_id",
    "last_modified_by_id",
    "created_date_time",
    "last_modified_date_time",
    "legacy_id"
        ${observations_to_insert_list}
)
(${concept_maps}
SELECT entity.id                                                                    "id",
       individual.id                                                                          "individual_id",
       entity.uuid                                                                  "uuid",
       entity.address_id                                                            "address_id",
       entity.enrolment_date_time                                                   "enrolment_date_time",
       entity.enrolment_location                                                    "enrolment_location",
       entity.is_voided                                                             "is_voided",
       entity.program_exit_date_time                                                "program_exit_date_time",
       entity.exit_location                                                         "exit_location",
       entity.created_by_id                                                         "created_by_id",
       entity.last_modified_by_id                                                   "last_modified_by_id",
       entity.created_date_time                                                     "created_date_time",
       entity.last_modified_date_time                                               "last_modified_date_time",
       entity.legacy_id                                                             "legacy_id"
       ${selections}
FROM public.program_enrolment entity
  ${cross_join_concept_maps}
         LEFT OUTER JOIN public.individual individual ON entity.individual_id = individual.id
WHERE entity.program_id = ${program_id}
  AND individual.subject_type_id = ${subject_type_id}
    and entity.last_modified_date_time > '${start_time}'
    and entity.last_modified_date_time <= '${end_time}');
