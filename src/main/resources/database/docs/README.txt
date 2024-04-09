Initial set of files used in PDDA PoC work.
The migration involves using Postgres instead of Oracle but some tables will need to exist on both databases.

The XHIBIT database and the XHIBITD tablespace were both created using the pgAdmin tool, so will need to be scripted.

Some conversion notes:
- When specifying a Tablespace in the create table script it needs to be in quotes
- Cannot use Varchar2, have amended to varchar to allow retention of field size. Another option would be to use Text
- Cannot use NUMBER with a field size, instead have specified Integer without a field size. Other options could and maybe preferred.