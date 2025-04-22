package uk.gov.hmcts.pdda.business.entities;

import com.pdda.hb.jpa.EntityManagerUtil;
import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfiguredpublicnotice.XhbConfiguredPublicNoticeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;

import java.util.List;
import java.util.Optional;

public class PddaEntityHelper {

    protected PddaEntityHelper() {
        // Protected constructor
    }

    public static EntityManager getEntityManager() {
        return EntityManagerUtil.getEntityManager();

    }

    // ---- Get repositories --- //

    private static XhbCourtSiteRepository getXCstrmRepo() {
        return new XhbCourtSiteRepository(getEntityManager());
    }
    
    private static XhbCourtRoomRepository getXCrtrmRepo() {
        return new XhbCourtRoomRepository(getEntityManager());
    }

    private static XhbCrLiveDisplayRepository getXcldRepo() {
        return new XhbCrLiveDisplayRepository(getEntityManager());
    }

    private static XhbScheduledHearingRepository getXshRepo() {
        return new XhbScheduledHearingRepository(getEntityManager());
    }

    private static XhbConfiguredPublicNoticeRepository getXCpnRepo() {
        return new XhbConfiguredPublicNoticeRepository(getEntityManager());
    }


    // ---- End get repositories --- //


    // XHB_CONFIGURED_PUBLIC_NOTICE
    public static Optional<XhbConfiguredPublicNoticeDao> xcpnFindByPrimaryKey(
        Integer configuredPublicNoticeId) {
        return getXCpnRepo().findByIdSafe(configuredPublicNoticeId);
    }

    public static Optional<XhbConfiguredPublicNoticeDao> cpnUpdate(
        XhbConfiguredPublicNoticeDao objToSave) {
        return getXCpnRepo().update(objToSave);
    }

    public static List<XhbConfiguredPublicNoticeDao> xcpnFindByDefinitivePnCourtRoomValue(
        Integer courtRoomId, Integer definitivePublicNublicNoticeId) {
        return getXCpnRepo().findByDefinitivePnCourtRoomValue(courtRoomId,
            definitivePublicNublicNoticeId);
    }

    // XHB_COURT_SITE
    public static Optional<XhbCourtSiteDao> xcstFindByPrimaryKey(Integer courtSiteId) {
        return getXCstrmRepo().findByIdSafe(courtSiteId);
    }
    
    // XHB_COURT_ROOM
    public static Optional<XhbCourtRoomDao> xcrtFindByPrimaryKey(Integer courtRoomId) {
        return getXCrtrmRepo().findByIdSafe(courtRoomId);
    }

    public static Optional<XhbCrLiveDisplayDao> xcldSave(XhbCrLiveDisplayDao objToSave) {
        return getXcldRepo().update(objToSave);
    }

    // XHB_SCHEDULED_HEARING
    public static Optional<XhbScheduledHearingDao> xshFindByPrimaryKey(Integer scheduledHearingId) {
        return getXshRepo().findByIdSafe(scheduledHearingId);
    }
}
