package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * Title: DataHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author HarrisM
 * @version 1.0
 */
public class DataHelper extends FinderHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DataHelper.class);

    public Optional<XhbCourtSiteDao> validateCourtSite(final Integer courtId,
        final String courtSiteName, final String courtSiteCode) {
        LOG.debug("validateCourtSite({})", courtSiteName);
        Optional<XhbCourtSiteDao> result = findCourtSite(courtId, courtSiteName);
        if (result.isEmpty()) {
            result = createCourtSite(courtId, courtSiteName, courtSiteCode);
        }
        return result;
    }

    public Optional<XhbCourtRoomDao> validateCourtRoom(final Integer courtSiteId,
        final String courtRoomName, final String description, final Integer crestCourtRoomNo) {
        LOG.debug("validateCourtRoom({})", crestCourtRoomNo);
        Optional<XhbCourtRoomDao> result = findCourtRoom(courtSiteId, crestCourtRoomNo);
        if (result.isEmpty()) {
            result = createCourtRoom(courtSiteId, courtRoomName, description, crestCourtRoomNo);
        }
        return result;
    }

    public Optional<XhbHearingListDao> validateHearingList(final Integer courtId,
        final Integer crestListId, final String listType, final String status,
        final LocalDateTime startDate) {
        LOG.debug("validateHearingList({},{})", status, startDate);
        Optional<XhbHearingListDao> result = findHearingList(courtId, status, startDate);
        if (result.isEmpty()) {
            result = createHearingList(courtId, crestListId, listType, status, startDate);
        }
        return result;
    }

    public Optional<XhbSittingDao> validateSitting(final Integer courtSiteId,
        final Integer courtRoomId, final String isFloating, final LocalDateTime sittingTime) {
        LOG.debug("validateSitting({})", sittingTime);
        Optional<XhbSittingDao> result = findSitting(courtSiteId, courtRoomId, sittingTime);
        if (result.isEmpty()) {
            result = createSitting(courtSiteId, courtRoomId, isFloating, sittingTime);
        }
        return result;
    }
}
