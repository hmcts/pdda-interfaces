package uk.gov.hmcts.pdda.business.services.publicnotice;

import uk.gov.hmcts.framework.exception.CsBusinessException;

/**

 * Title:PublicNoticeCourtRoomUnknownException :- thrown if courtRoom is not known in the public
 * Notice Subsystem.


 * Description: see title


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @authors Pat Fox
 * @version 1.0
 */

public class PublicNoticeCourtRoomUnknownException extends CsBusinessException {
    static final long serialVersionUID = -642601943262533056L;

    private static final String ERROR_KEY = "publicnotice.courtroom.notfound";

    private static final String LOG_MESSAGE = "Could not find court room for id: ";

    public PublicNoticeCourtRoomUnknownException(Integer courtRoomId) {
        super(ERROR_KEY, LOG_MESSAGE + courtRoomId);
    }
}
