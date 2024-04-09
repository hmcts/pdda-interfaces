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
	l_document_name := 'DailyList_' || l_court_code || '_' || to_char(l_doc_timestamp, 'YYYYMMDDHH24MISS') || '.xml';
	l_publish_time := to_char(l_doc_timestamp, 'YYYY-MM-DD') || 'T' || to_char(l_doc_timestamp, 'HH24:MI:SS.000');
	
	l_clob_data := '<?xml version="1.0" encoding="UTF-8"?>'
			|| '<cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"'
			|| ' xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"'
			|| ' xmlns="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"'
			|| ' xmlns:p2="http://www.govtalk.gov.uk/people/bs7666"' 
			|| ' xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' 
			|| ' xsi:schemaLocation="http://www.courtservice.gov.uk/schemas/courtservice DailyList-v1-0.xsd">'
			|| '    <cs:DocumentID>' || '        <cs:DocumentName>' || l_document_name || '</cs:DocumentName>'
			|| '        <cs:UniqueID>0149051e-9db2-4b47-999e-fef76909ee73</cs:UniqueID>'
			|| '        <cs:DocumentType>DL</cs:DocumentType>' || '    </cs:DocumentID>' || '    <cs:ListHeader>'
			|| '        <cs:ListCategory>Criminal</cs:ListCategory>' || '        <cs:StartDate>' || to_char(l_doc_timestamp, 'YYYY-MM-DD') || '</cs:StartDate>'
			|| '        <cs:EndDate>' || to_char(l_doc_timestamp, 'YYYY-MM-DD') || '</cs:EndDate>' || '        <cs:Version>NOT VERSIONED</cs:Version>'
			|| '        <cs:PublishedTime>' || l_publish_time || '</cs:PublishedTime>'
			|| '        <cs:CRESTlistID>510</cs:CRESTlistID>' || '    </cs:ListHeader>' || '    <cs:CrownCourt>'
			|| '        <cs:CourtHouseType>Crown Court</cs:CourtHouseType>'
			|| '        <cs:CourtHouseCode CourtHouseShortName="SNARE">' || l_court_code || '</cs:CourtHouseCode>'
			|| '        <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>' || '    </cs:CrownCourt>'
			|| '    <cs:CourtLists>' || '        <cs:CourtList>' || '            <cs:CourtHouse>'
			|| '                <cs:CourtHouseType>Crown Court</cs:CourtHouseType>'
			|| '                <cs:CourtHouseCode>' || l_court_code || '</cs:CourtHouseCode>'
			|| '                <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>' || '            </cs:CourtHouse>'
			|| '            <cs:Sittings>' || '                <cs:Sitting>'
			|| '                    <cs:CourtRoomNumber>235</cs:CourtRoomNumber>'
			|| '                    <cs:SittingSequenceNo>1</cs:SittingSequenceNo>'
			|| '                    <cs:SittingPriority>T</cs:SittingPriority>' || '                    <cs:Judiciary>'
			|| '                        <cs:Judge>'
			|| '                            <apd:CitizenNameSurname>Van-JUDGE</apd:CitizenNameSurname>'
			|| '                            <apd:CitizenNameRequestedName>Freddy</apd:CitizenNameRequestedName>'
			|| '                        </cs:Judge>' || '                    </cs:Judiciary>'
			|| '                    <cs:Hearings>' || '                        <cs:Hearing>'
			|| '                            <cs:HearingSequenceNumber>1</cs:HearingSequenceNumber>'
			|| '                            <cs:HearingDetails HearingType="TRL">'
			|| '                                <cs:HearingDescription>For Trial</cs:HearingDescription>'
			|| '                                <cs:HearingDate>' || to_char(l_doc_timestamp, 'YYYY-MM-DD') || '</cs:HearingDate>'
			|| '                            </cs:HearingDetails>'
			|| '                            <cs:CaseNumber>92AD685737</cs:CaseNumber>'
			|| '                            <cs:Prosecution ProsecutingAuthority="Crown Prosecution Service">'
			|| '                                <cs:ProsecutingReference>92AD685737</cs:ProsecutingReference>'
			|| '                            </cs:Prosecution>' || '                            <cs:Defendants>'
			|| '                                <cs:Defendant>'
			|| '                                    <cs:PersonalDetails>'
			|| '                                        <cs:Name>'
			|| '                                            <apd:CitizenNameSurname>Van</apd:CitizenNameSurname>'
			|| '                                            <apd:CitizenNameRequestedName>Sri</apd:CitizenNameRequestedName>'
			|| '                                        </cs:Name>'
			|| '                                        <cs:IsMasked>no</cs:IsMasked>'
			|| '                                    </cs:PersonalDetails>'
			|| '                                    <cs:Charges>'
			|| '                                        <cs:Charge CJSoffenceCode="TH68001A" IndictmentCountNumber="0">'
			|| '                                            <cs:OffenceStatement>Attempted theft</cs:OffenceStatement>'
			|| '                                        </cs:Charge>'
			|| '                                        <cs:Charge CJSoffenceCode="HC57001" IndictmentCountNumber="0">'
			|| '                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>'
			|| '                                            <cs:OffenceStatement>Manslaughter on grounds of diminished responsibility</cs:OffenceStatement>'
			|| '                                        </cs:Charge>'
			|| '                                    </cs:Charges>' || '                                </cs:Defendant>'
			|| '                                <cs:Defendant>'
			|| '                                    <cs:PersonalDetails>'
			|| '                                        <cs:Name>'
			|| '                                            <apd:CitizenNameSurname>CPPSECOND</apd:CitizenNameSurname>'
			|| '                                            <apd:CitizenNameRequestedName>Another</apd:CitizenNameRequestedName>'
			|| '                                        </cs:Name>'
			|| '                                        <cs:IsMasked>no</cs:IsMasked>'
			|| '                                    </cs:PersonalDetails>'
			|| '                                    <cs:Charges>'
			|| '                                        <cs:Charge CJSoffenceCode="TH68001A" IndictmentCountNumber="0">'
			|| '                                            <cs:OffenceStatement>Attempted theft</cs:OffenceStatement>'
			|| '                                        </cs:Charge>'
			|| '                                        <cs:Charge CJSoffenceCode="HC57001" IndictmentCountNumber="0">'
			|| '                                            <cs:CRESTchargeID>453S00649408</cs:CRESTchargeID>'
			|| '                                            <cs:OffenceStatement>Manslaughter on grounds of diminished responsibility</cs:OffenceStatement>'
			|| '                                        </cs:Charge>'
			|| '                                    </cs:Charges>' || '                                </cs:Defendant>'
			|| '                            </cs:Defendants>' || '                        </cs:Hearing>'
			|| '                    </cs:Hearings>' || '                </cs:Sitting>' || '            </cs:Sittings>'
			|| '        </cs:CourtList>' || '    </cs:CourtLists>' || '</cs:DailyList>';
			
	insert into xhb_clob (clob_id, clob_data) values (l_clob_id, l_clob_data);
	
	insert into xhb_cpp_staging_inbound (document_name, court_code, document_type, time_loaded, clob_id, validation_status, processing_status)
	values (l_document_name, l_court_code, 'DL', localtimestamp, l_clob_id, 'VS', 'NP');
	
end;
$$