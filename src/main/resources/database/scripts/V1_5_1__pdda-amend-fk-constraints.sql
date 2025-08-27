-- V1_5_1__pdda-amend-fk-constraints.sql
ALTER TABLE pdda.xhb_pdda_message
  DROP CONSTRAINT IF EXISTS pddamsg_clob_fk;

