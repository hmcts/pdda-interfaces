SET client_encoding TO 'UTF8';

create index idx_xhb_clob_id on pdda.xhb_clob(clob_id);
create index idx_xhb_cpp_staging_inbound_id on pdda.xhb_cpp_staging_inbound(cpp_staging_inbound_id);
create index idx_xhbclob_xhbcppstaginb_id on pdda.xhb_cpp_staging_inbound(clob_id);
