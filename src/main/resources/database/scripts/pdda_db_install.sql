\i 'V1_1__pdda-create-tables.sql'
\i 'V1_2__pdda-create-audit-tables.sql'
\cd ..
\cd data
\i 'load_ref_data.sql'
\cd ..
\i 'V1_4__pdda-create-indexes.sql'
\i 'V1_5__pdda-create-fk-constraints.sql'
\i 'V1_6__pdda-create-sequences.sql'
\i 'V1_7__pdda-reset-sequences.sql'
\i 'V1_8__pdda-create-triggers.sql'
\cd packages
\i 'pdda-xhb_custom_pkg.sql'
\i 'pdda-xhb_public_display_pkg.sql'
\cd ..

-- Housekeeping package including installation of ORAFCE extention.  Comment out as necessary
-- COMMENT OUT FOR MACBOOK UNLESS CAN GET ORAFCE INSTALLED ON MACOS
--\i 'pdda_install_housekeeping.sql'

ANALYZE;
