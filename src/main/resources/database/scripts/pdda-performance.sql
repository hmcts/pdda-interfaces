SET client_encoding TO 'UTF8';

-- Remove duplicates in XHB_DISPLAY_STORE
WITH ranked AS (
  SELECT
    display_store_id,
    row_number() OVER (
      PARTITION BY retrieval_code
      ORDER BY last_update_date DESC NULLS LAST, display_store_id DESC
    ) AS rn
  FROM pdda.xhb_display_store
  WHERE (obs_ind IS NULL OR obs_ind IN ('N',' '))
)
DELETE FROM pdda.xhb_display_store s
USING ranked r
WHERE s.display_store_id = r.display_store_id
  AND r.rn > 1;
  
-- Update garbage collection config on XHB_DISPLAY_STORE
ALTER TABLE pdda.xhb_display_store
SET (
  autovacuum_vacuum_scale_factor = 0.01,
  autovacuum_vacuum_threshold = 50
);

-- New indexes
CREATE INDEX idx_xhb_display_store_last_update_date
ON pdda.xhb_display_store (last_update_date DESC);

CREATE UNIQUE INDEX ux_xhb_display_store_retrieval_code
ON pdda.xhb_display_store (retrieval_code);

-- Have a database transaction timeout 
ALTER DATABASE pdda
SET idle_in_transaction_session_timeout = '10min';