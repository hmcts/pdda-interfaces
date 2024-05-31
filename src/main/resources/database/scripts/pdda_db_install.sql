\i 'V1_1__pdda-create-tables.sql'
\i 'V2_1__pdda-create-tables.sql'
\i 'V3_1__pdda-create-tables.sql'
\i 'V1_2__pdda-create-audit-tables.sql'
\i 'V2_2__pdda-create-audit-tables.sql'
\i 'V3_2__pdda-create-audit-tables.sql'
\cd ..
\cd data
\i 'load_ref_data.sql'
\cd ..
\cd scripts
\i 'V1_4__pdda-create-indexes.sql'
\i 'V2_4__pdda-create-indexes.sql'
\i 'V3_4__pdda-create-indexes.sql'
\i 'V1_5__pdda-create-fk-constraints.sql'
\i 'V2_5__pdda-create-fk-constraints.sql'
\i 'V3_5__pdda-create-fk-constraints.sql'
\i 'V1_6__pdda-create-sequences.sql'
\i 'V2_6__pdda-create-sequences.sql'
\i 'V3_6__pdda-create-sequences.sql'
\i 'V1_7__pdda-reset-sequences.sql'
\i 'V2_7__pdda-reset-sequences.sql'
\i 'V3_7__pdda-reset-sequences.sql'
\i 'V1_8__pdda-create-triggers.sql'
\i 'V2_8__pdda-create-triggers.sql'
\i 'V3_8__pdda-create-triggers.sql'
\cd ..
\cd packages
\i 'V1_9__pdda-xhb_custom_pkg.sql'
\i 'V1_10__pdda-xhb_public_display_pkg.sql'
\cd ..
\cd scripts

-- Housekeeping package including installation of ORAFCE extention.  Comment out as necessary
-- COMMENT OUT FOR MACBOOK UNLESS CAN GET ORAFCE INSTALLED ON MACOS
--\i 'pdda_install_housekeeping.sql'

ANALYZE;
