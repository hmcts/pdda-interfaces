SET client_encoding TO 'UTF8';

-- Enforce uniqueness when both columns are non-null
CREATE UNIQUE INDEX IF NOT EXISTS pdda_sh_sitting_hrg_notnull
ON pdda.xhb_scheduled_hearing (sitting_id, hearing_id)
WHERE sitting_id IS NOT NULL AND hearing_id IS NOT NULL;
