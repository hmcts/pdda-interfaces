\i 'pdda-create-tables.sql'
\i 'pdda-create-audit-tables.sql'
\cd data
\i 'load_ref_data.sql'
\cd ..
\i 'pdda-create-indexes.sql'
\i 'pdda-create-fk-constraints.sql'
\i 'pdda-create-sequences.sql'
\i 'pdda-reset-sequences.sql'
\i 'pdda-create-triggers.sql'
\cd packages
\i 'pdda-xhb_custom_pkg.sql'
\i 'pdda-xhb_public_display_pkg.sql'
\cd ..

-- Housekeeping package including installation of ORAFCE extention.  Comment out as necessary
-- COMMENT OUT FOR MACBOOK UNLESS CAN GET ORAFCE INSTALLED ON MACOS
--\i 'pdda_install_housekeeping.sql'

ANALYZE;
