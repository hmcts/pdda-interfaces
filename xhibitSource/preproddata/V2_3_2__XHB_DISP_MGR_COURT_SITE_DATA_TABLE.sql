Insert into XHB_DISP_MGR_COURT_SITE
   (COURT_SITE_ID, TITLE, PAGE_URL, SCHEDULE_ID, XHIBIT_COURT_SITE_ID, 
    LAST_UPDATE_DATE, CREATION_DATE, CREATED_BY, LAST_UPDATED_BY, VERSION, 
    RAG_STATUS, RAG_STATUS_DATE, NOTIFICATION)
 Values
   (2, 'Test Court - Swansea', 'http://xhibitdisplay.staging.internal.hmcts.net/PublicDisplay/FileServlet?', 2, 1608, 
    TO_DATE('7/12/2024 3:37:47 PM', 'MM/DD/YYYY HH:MI:SS AM'), TO_DATE('3/6/2017 2:19:41 PM', 'MM/DD/YYYY HH:MI:SS AM'), 'pp_court_clerk_1', 'XHIBIT', 314210, 
    'R', TO_DATE('7/11/2024 10:57:00 AM', 'MM/DD/YYYY HH:MI:SS AM'), 'Test site - Swansea');
Insert into XHB_DISP_MGR_COURT_SITE
   (COURT_SITE_ID, TITLE, PAGE_URL, SCHEDULE_ID, XHIBIT_COURT_SITE_ID, 
    LAST_UPDATE_DATE, CREATION_DATE, CREATED_BY, LAST_UPDATED_BY, VERSION, 
    RAG_STATUS, RAG_STATUS_DATE, NOTIFICATION)
 Values
   (1, 'Test Court - York', 'http://xhibitdisplay.staging.internal.hmcts.net/PublicDisplay/FileServlet?', 1, 218, 
    TO_DATE('7/20/2024 5:24:00 PM', 'MM/DD/YYYY HH:MI:SS AM'), TO_DATE('7/12/2024 3:36:35 PM', 'MM/DD/YYYY HH:MI:SS AM'), 'pp_court_clerk_1', 'XHIBIT', 2587, 
    'A', TO_DATE('7/20/2024 4:24:00 PM', 'MM/DD/YYYY HH:MI:SS AM'), 'Test site - York');
Insert into XHB_DISP_MGR_COURT_SITE
   (COURT_SITE_ID, TITLE, PAGE_URL, SCHEDULE_ID, XHIBIT_COURT_SITE_ID, 
    LAST_UPDATE_DATE, CREATION_DATE, CREATED_BY, LAST_UPDATED_BY, VERSION, 
    RAG_STATUS, RAG_STATUS_DATE, NOTIFICATION)
 Values
   (3, 'Test Court - Isleworth', 'http://xhibitdisplay.staging.internal.hmcts.net/PublicDisplay/FileServlet?', 1, 1607, 
    TO_DATE('8/5/2024 10:18:00 AM', 'MM/DD/YYYY HH:MI:SS AM'), TO_DATE('7/24/2024 12:26:59 PM', 'MM/DD/YYYY HH:MI:SS AM'), 'pp_court_clerk_1', 'XHIBIT', 4604, 
    'A', TO_DATE('8/5/2024 9:18:00 AM', 'MM/DD/YYYY HH:MI:SS AM'), 'Test site - Isleworth');
COMMIT;
