do $$

declare

	l_content text;
	l_doc_store_id bigint;

begin
	
	SELECT nextval('xhb_display_store_seq') INTO STRICT l_doc_store_id;
	
	l_content := '<b>This is a Display URI Test</b>';
	
	insert into xhb_display_store (display_store_id, retrieval_code, content, obs_ind)
	values (l_doc_store_id, 'displays-snaresbrook-453-firstfloor-courtroom101display', l_content, 'N');
	
	SELECT nextval('xhb_display_store_seq') INTO STRICT l_doc_store_id;
	
	l_content := '<b>This is a Display Document URI Test</b>';
	
	insert into xhb_display_store (display_store_id, retrieval_code, content, obs_ind)
	values (l_doc_store_id, 'documents-court82-1-2-3-DailyList', l_content, 'N');
	
end;
$$