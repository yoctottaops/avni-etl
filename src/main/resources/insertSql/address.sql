insert into ${schema_name}.address ("${titleColumnName}", "${idColumnName}", id, "uuid", "is_voided", "created_by_id", "last_modified_by_id",
                                    "created_date_time", "last_modified_date_time")
select al.title,
       al.id,
       al.id,
       al.uuid,
       al.is_voided,
       al.created_by_id,
       al.last_modified_by_id,
       al.created_date_time,
       al.last_modified_date_time
from address_level al
         join address_level_type alt on al.type_id = alt.id
where alt.name = '${titleColumnName}'
  and al.last_modified_date_time > '${start_time}'
  and al.last_modified_date_time <= '${end_time}';
