insert into ${schema_name}.address ("${columnName}", "lowest_level_id")
select al.title,
       al.id
from address_level al
         join address_level_type alt on al.type_id = alt.id
where alt.name = '${columnName}'
  and al.last_modified_date_time > '${start_time}'
  and al.last_modified_date_time <= '${end_time}';
