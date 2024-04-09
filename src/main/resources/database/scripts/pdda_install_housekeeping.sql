CREATE EXTENSION orafce;
-- Change the directory here accordingly
INSERT INTO UTL_FILE.UTL_FILE_DIR (dir, dirname) VALUES('C:/PDDA-Database-Objects','HK_LOGS');
\cd packages
\i 'pdda-xhb_housekeeping_pkg.sql'
