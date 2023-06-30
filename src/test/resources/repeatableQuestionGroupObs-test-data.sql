insert into encounter (id, observations, encounter_date_time, encounter_type_id, individual_id, uuid, version, organisation_id, is_voided, audit_id, encounter_location,
                       earliest_visit_date_time, max_visit_date_time, cancel_date_time, cancel_observations, cancel_location, name, legacy_id, created_by_id,
                       last_modified_by_id, created_date_time, last_modified_date_time, address_id, sync_concept_1_value, sync_concept_2_value)
values (2001, '{
  "6ed176f6-70d5-4db8-8f39-68c06f3459af": [
    {
      "0270c64f-6201-46b3-b126-2913af80a065": 100,
      "14a66108-aaf8-4977-aba0-91027af1b1ec": "FTX"
    },
    {
      "0270c64f-6201-46b3-b126-2913af80a065": 20,
      "14a66108-aaf8-4977-aba0-91027af1b1ec": "Binance"
    }
  ]
}', null, 1054, 574170, '5765fefc-8297-49e6-91ca-6ac9b797935d', 0, 12, false, create_audit(), null, null, null, null, '{}', null, null, null,
        1, 1, '2022-04-06 07:15:07.000 +00:00', '2022-04-06 07:15:07.000 +00:00', 107786, null, null);
