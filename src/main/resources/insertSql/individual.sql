--[Data Extract Report] Registration
insert into ${schema_name}.${table_name} (
    "id", "address_id", "uuid", "registration_date", "registration_location",
    "is_voided", "created_by_id", "last_modified_by_id", "created_date_time",
    "last_modified_date_time", "legacy_id"
        ${observations_to_insert_list}
)
    (${concept_maps}
        SELECT individual.id                                                                as "id",
        individual.address_id                                                               as "address_id",
        individual.uuid                                                                     as "uuid",
        individual.registration_date                                                        as "registration_date",
        individual.registration_location                                                    as "registration_location",
        individual.is_voided                                                                as "is_voided",
        individual.created_by_id                                                            as "created_by_id",
        individual.last_modified_by_id                                                      as "last_modified_by_id",
        individual.created_date_time                                                        as "created_date_time",
        individual.last_modified_date_time                                                  as "last_modified_date_time",
        individual.legacy_id                                                                as "legacy_id"
        ${selections}
        FROM public.individual individual
        ${cross_join_concept_maps}
        LEFT OUTER JOIN public.gender g ON g.id = individual.gender_id
        LEFT OUTER JOIN public.address_level a ON individual.address_id = a.id
        where individual.subject_type_id = ${subject_type_id}
        and individual.last_modified_date_time > '${start_time}'
        and individual.last_modified_date_time <= '${end_time}')
