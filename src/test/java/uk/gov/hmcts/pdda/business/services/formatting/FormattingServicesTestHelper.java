package uk.gov.hmcts.pdda.business.services.formatting;

import uk.gov.hmcts.DummyFormattingUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;

/**

 * Title: FormattingServicesTestHelper.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */

class FormattingServicesTestHelper {

    private static final String DOCTYPE_DAILY_LIST = "DL";
    
    protected XhbCppListDao getXhbCppListDao(XhbClobDao xhbClobDao) {
        XhbCppListDao xhbCppListDao = DummyFormattingUtil.getXhbCppListDao();
        xhbCppListDao.setListClobId(xhbClobDao.getClobId());
        xhbCppListDao.setListType(DOCTYPE_DAILY_LIST);
        return xhbCppListDao;
    }
}
