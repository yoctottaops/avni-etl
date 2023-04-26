select count(*)
from public.table_metadata tm
         inner join public.column_metadata cm
                    on tm.id = cm.table_id
where tm.name = 'address'
  and tm.schema_name = :schemaName
  and cm.name in (:addressLevelTypeNames);
