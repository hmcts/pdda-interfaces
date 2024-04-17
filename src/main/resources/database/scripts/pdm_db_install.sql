\i 'V1_1__pdm-create-tables.sql'
\i 'V1_2__pdm-create-audit-tables.sql'
\i 'xhb_court_site_welsh.sql'
\cd ..
\cd data
\i 'load_pdm_ref_data.sql'
\cd ..
\cd scripts
\i 'V1_4__pdm-create-indexes.sql'
\i 'V1_5__pdm-create-fk-constraints.sql'
\i 'V1_6__pdm-create-sequences.sql'
\i 'V1_7__pdm-reset-sequences.sql'
\i 'V1_8__pdm-create-triggers.sql'
\cd ..
\cd packages
\i 'V1_9__pdm-xhb_custom_pkg.sql'
\i 'V1_10__pdm-xhb_disp_mgr_housekeeping_pkg.sql'
\i 'V1_11__pdm-xhb_disp_mgr_pkg.sql'
\cd ..
\cd scripts

ANALYZE;
