do $$

declare

	l_doc_timestamp	timestamp;
	l_clob_data text;
	l_court_code integer;
	l_document_name character varying(50);
	l_publish_time character varying(50);
	l_clob_id bigint;

begin
	
	SELECT nextval('xhb_clob_seq') INTO STRICT l_clob_id;
	l_doc_timestamp := NOW()::timestamp;	
	l_court_code := 453;
	l_document_name := 'WebPage_' || l_court_code || '_' || to_char(l_doc_timestamp, 'YYYYMMDDHH24MISS') || '.xml';
	l_publish_time := to_char(l_doc_timestamp, 'YYYY-MM-DD') || 'T' || to_char(l_doc_timestamp, 'HH24:MI:SS.000');
	
	l_clob_data := '<?xml version="1.0" encoding="UTF-8"?>'
			|| '<?xml-stylesheet type="text/xsl" href="InternetWebPageTemplate.xsl"?>'
			|| '<currentcourtstatus xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' || '  <court>'
			|| '    <courtname>SNARESBROOK</courtname>' || '    <courtsites>' || '      <courtsite>'
			|| '        <courtsitename>SNARESBROOK mu</courtsitename>' || '        <courtrooms>'
			|| '          <courtroom>' || '            <cases>' || '              <caseDetails>'
			|| '                <casenumber>20200028</casenumber>' || '                <casetype>T</casetype>'
			|| '                <hearingtype>Appeal (Part Heard)</hearingtype>' || '              </caseDetails>'
			|| '            </cases>' || '            <defendants>' || '              <defendant>'
			|| '                <firstname>cppME</firstname>' || '                <lastname>MAcpp</lastname>'
			|| '              </defendant>' || '            </defendants>' || '            <currentstatus>'
			|| '              <event>' || '                <time>11:11</time>'
			|| '                <date>05/08/20</date>'
			|| '                <free_text>My new reporting restriction</free_text>'
			|| '                <type>CPP</type>' || '              </event>' || '            </currentstatus>'
			|| '            <timestatusset>10:10</timestatusset>'
			|| '            <courtroomname>Court 2</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 2</courtroomname>' || '          </courtroom>'
			|| '        </courtrooms>' || '      </courtsite>' || '      <courtsite>'
			|| '        <courtsitename>PRESTON3</courtsitename>' || '        <courtrooms>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 1</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 2</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 3</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 5</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 10</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 11</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 12</courtroomname>' || '          </courtroom>'
			|| '        </courtrooms>' || '      </courtsite>' || '      <courtsite>'
			|| '        <courtsitename>VILNIUS</courtsitename>' || '        <courtrooms>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 9</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 10</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 11</courtroomname>' || '          </courtroom>'
			|| '        </courtrooms>' || '      </courtsite>' || '      <courtsite>'
			|| '        <courtsitename>VILLAGE HALL</courtsitename>' || '        <courtrooms>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 5</courtroomname>' || '          </courtroom>'
			|| '        </courtrooms>' || '      </courtsite>' || '      <courtsite>'
			|| '        <courtsitename>Lewes/Brighton/Hove</courtsitename>' || '        <courtrooms>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 1</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 2</courtroomname>' || '          </courtroom>'
			|| '          <courtroom>' || '            <currentstatus/>'
			|| '            <courtroomname>Court 3</courtroomname>' || '          </courtroom>'
			|| '        </courtrooms>' || '      </courtsite>' || '    </courtsites>' || '  </court>'
			|| '  <datetimestamp>' || '    <dayofweek>Wednesday</dayofweek>' || '    <date>06</date>'
			|| '    <month>August</month>' || '    <year>2020</year>' || '    <hour>10</hour>'
			|| '    <min>10</min>' || '  </datetimestamp>' || '  <pagename>snaresbrook</pagename>'
			|| '</currentcourtstatus>';
			
	insert into xhb_clob (clob_id, clob_data) values (l_clob_id, l_clob_data);
	
	insert into xhb_cpp_staging_inbound (document_name, court_code, document_type, time_loaded, clob_id, validation_status)
	values (l_document_name, l_court_code, 'WP', localtimestamp, l_clob_id, 'NP');
	
end;
$$