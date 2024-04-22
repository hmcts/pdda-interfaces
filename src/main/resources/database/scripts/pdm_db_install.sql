\i 'V2_1__pdm-create-tables.sql'
\i 'V2_2__pdm-create-audit-tables.sql'
\cd ..
\cd data
\i 'load_pdm_ref_data.sql'
\cd ..
\cd scripts
\i 'V2_4__pdm-create-indexes.sql'
\i 'V2_5__pdm-create-fk-constraints.sql'
\i 'V2_6__pdm-create-sequences.sql'
\i 'V2_7__pdm-reset-sequences.sql'
\i 'V2_8__pdm-create-triggers.sql'
\cd ..
\cd packages
\i 'V2_9__pdm-xhb_custom_pkg.sql'
\i 'V2_10__pdm-xhb_disp_mgr_housekeeping_pkg.sql'
\i 'V2_11__pdm-xhb_disp_mgr_pkg.sql'
\cd ..
\cd scripts

ANALYZE;
