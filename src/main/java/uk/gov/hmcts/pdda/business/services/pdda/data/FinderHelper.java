package uk.gov.hmcts.pdda.business.services.pdda.data;

import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * Title: FinderHelper.
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
@SuppressWarnings("PMD.LawOfDemeter")
public class FinderHelper extends CreationHelper {

    public FinderHelper() {
        super();
    }

    // JUnit constructor
    public FinderHelper(RepositoryHelper repositoryHelper) {
        super(repositoryHelper);
    }

    public Optional<XhbCourtSiteDao> findCourtSite(final Integer courtId,
        final String courtSiteName) {
        return getRepositoryHelper().getXhbCourtSiteRepository().findByCourtSiteName(courtId,
            courtSiteName);
    }

    public Optional<XhbCourtRoomDao> findCourtRoom(final Integer courtId,
        final Integer crestCourtRoomNo) {
        return getRepositoryHelper().getXhbCourtRoomRepository().findByCourtRoomNo(courtId,
            crestCourtRoomNo);
    }

    public Optional<XhbHearingListDao> findHearingList(final Integer courtId, final String status,
        final LocalDateTime startDate) {
        return getRepositoryHelper().getXhbHearingListRepository()
            .findByCourtIdStatusAndDate(courtId, status, startDate);
    }

    public Optional<XhbSittingDao> findSitting(final Integer courtSiteId, final Integer courtRoomId,
        final LocalDateTime sittingTime) {
        return getRepositoryHelper().getXhbSittingRepository()
            .findByCourtRoomAndSittingTime(courtSiteId, courtRoomId, sittingTime);
    }
}
