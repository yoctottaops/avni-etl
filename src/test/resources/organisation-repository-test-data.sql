insert into organisation (id, name, db_user, uuid, parent_organisation_id, is_voided, media_directory, username_suffix,
                          account_id, schema_name, has_analytics_db)
values (13, 'Org group item 1', 'ogi1', uuid_generate_v4(), null, false, 'ogi1', 'ogi1', 1, 'ogi1', false);

insert into organisation (id, name, db_user, uuid, parent_organisation_id, is_voided, media_directory, username_suffix,
                          account_id, schema_name, has_analytics_db)
values (14, 'Org group item 2', 'ogi2', uuid_generate_v4(), null, false, 'ogi2', 'ogi2', 1, 'ogi2', false);

insert into organisation_group(id, name, db_user, account_id, has_analytics_db, schema_name)
VALUES (11, 'Org group', 'og', 1, true, 'og');

insert into organisation_group_organisation (id, name, organisation_group_id, organisation_id)
VALUES (11, 'OGI1', 11, 13);

insert into organisation_group_organisation (id, name, organisation_group_id, organisation_id)
VALUES (12, 'OGI2', 11, 14);