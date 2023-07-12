select cd.name name,
       program.name program_name,
       st.name subject_type_name
       from checklist_detail cd
    join program on program.name = 'Child'
    join form_mapping fm on fm.entity_id = program.id
    join form f on fm.form_id = f.id
    join subject_type st on st.id = fm.subject_type_id
where f.form_type = 'ProgramEnrolment';