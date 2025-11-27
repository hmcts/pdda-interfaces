ALTER TABLE xhb_hearing
DROP CONSTRAINT IF EXISTS hearing_unique;

ALTER TABLE xhb_hearing
ADD CONSTRAINT hearing_unique
UNIQUE (court_id, case_id, ref_hearing_type_id, hearing_start_date);
