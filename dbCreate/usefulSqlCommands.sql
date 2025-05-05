SELECT table_name FROM user_tables;


SELECT trigger_name, table_name, triggering_event, status
FROM user_triggers;



SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM user_sequences;


set pagesize 50000

set linesize 500