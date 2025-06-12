SET client_encoding TO 'UTF8';

-- Tables not referenced anywhere in the code or in any packages --
DROP TABLE IF EXISTS mtbl_case_history;
DROP TABLE IF EXISTS xhb_cc_info;

-- Tables referenced in the old housekeeping package --
DROP TABLE IF EXISTS xhb_hk_cpp_error_log;
DROP TABLE IF EXISTS xhb_hk_cpp_results;
DROP TABLE IF EXISTS xhb_hk_error_log;
DROP TABLE IF EXISTS xhb_hk_results;
