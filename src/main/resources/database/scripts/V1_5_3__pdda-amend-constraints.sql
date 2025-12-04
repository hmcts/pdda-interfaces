ALTER TABLE xhb_sitting
DROP CONSTRAINT IF EXISTS sitting_unique;

ALTER TABLE xhb_sitting
ADD CONSTRAINT sitting_unique
UNIQUE (sitting_time, court_room_id, court_site_id);
