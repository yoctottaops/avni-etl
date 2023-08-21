select u.id,
       u.username username,
       u.uuid uuid,
       u.organisation_id organisation_id,
       case when count(p.id) > 0 or bool_or(g.has_all_privileges) = true then true else false end as has_analytics_access,
       case when count(aa.id) > 0 then true else false end                                        as is_admin
from users u
         left join account_admin aa on u.id = aa.admin_id
         left join user_group ug on u.id = ug.user_id and ug.is_voided is false
         left join groups g on ug.group_id = g.id
         left join group_privilege gp on ug.id = gp.group_id and gp.is_voided is false
         left join privilege p on gp.privilege_id = p.id and p.name = 'Analytics'
where u.uuid = :uuid
group by u.id, u.username, u.uuid, u.organisation_id;
