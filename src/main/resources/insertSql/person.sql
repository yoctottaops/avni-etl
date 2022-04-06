--[Data Extract Report] Registration
insert into ${schema_name}.${table_name} (
    "id", "address_id", "uuid", "first_name", "last_name", "gender", "date_of_birth",
    "date_of_birth_verified", "registration_date", "registration_location",
    "is_voided", "created_by_id", "last_modified_by_id", "created_date_time",
    "last_modified_date_time", "legacy_id"
    ${observations_to_insert_list}
)
(${concept_maps}
SELECT individual.id                                                                       as "id",
       individual.address_id                                                               as "address_id",
       individual.uuid                                                                     as "uuid",
       individual.first_name                                                               as "first_name",
       individual.last_name                                                                as "last_name",
       g.name                                                                              as "gender",
       individual.date_of_birth                                                            as "date_of_birth",
       individual.date_of_birth_verified                                                   as "date_of_birth_verified",
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
where individual.subject_type_id = ${subject_type_id}
       and individual.last_modified_date_time > '${start_time}'
       and individual.last_modified_date_time <= '${end_time}')
