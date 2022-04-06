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
SELECT programEnrolment.id                                                                    "id",
       individual.id                                                                          "individual_id",
       programEnrolment.uuid                                                                  "uuid",
       programEnrolment.address_id                                                            "address_id",
       programEnrolment.enrolment_date_time                                                   "enrolment_date_time",
       programEnrolment.enrolment_location                                                    "enrolment_location",
       programEnrolment.is_voided                                                             "is_voided",
       programEnrolment.program_exit_date_time                                                "program_exit_date_time",
       programEnrolment.exit_location                                                         "exit_location",
       programEnrolment.created_by_id                                                         "created_by_id",
       programEnrolment.last_modified_by_id                                                   "last_modified_by_id",
       programEnrolment.created_date_time                                                     "created_date_time",
       programEnrolment.last_modified_date_time                                               "last_modified_date_time",
       programEnrolment.legacy_id                                                             "legacy_id"
       ${selections}
FROM public.program_enrolment programEnrolment
  ${cross_join_concept_maps}
         LEFT OUTER JOIN public.individual individual ON programEnrolment.individual_id = individual.id
WHERE programEnrolment.program_id = '${program_id}'
  AND individual.subject_type_id = '${subject_type_id}'
    and programEnrolment.last_modified_date_time > '${start_time}'
    and programEnrolment.last_modified_date_time <= '${end_time}');
