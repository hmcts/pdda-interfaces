package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.PddaSftpHelperSshj;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfigHelper;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpHelperUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

/**

 * Title: Xhibit PDDAHelper.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Mark Harris
 * @version 1.0
 */
@SuppressWarnings("PMD")
public abstract class XhibitPddaHelper extends PddaConfigHelper {
    private static final Logger LOG = LoggerFactory.getLogger(XhibitPddaHelper.class);

    private PublicDisplayNotifier publicDisplayNotifier;
    private XhbCaseRepository caseRepository;
    private XhbCourtRepository courtRepository;
    private XhbClobRepository clobRepository;
    private XhbCourtRoomRepository courtRoomRepository;
    private XhbCourtSiteRepository courtSiteRepository;
    private XhbHearingRepository hearingRepository;
    private XhbScheduledHearingRepository scheduledHearingRepository;
    private XhbSittingRepository sittingRepository;
    private PddaMessageHelper pddaMessageHelper;
    private CppStagingInboundHelper cppStagingInboundHelper;
    private PddaSftpHelper pddaSftpHelper;
    private SftpConfigHelper sftpConfigHelper;
    private SftpHelperUtil sftpHelperUtil;
    private PddaSftpHelperSshj pddaSftpHelperSshj;

    protected XhibitPddaHelper(EntityManager entityManager, Environment environment) {
        super(entityManager, environment);
    }

    // Junit constructor
    protected XhibitPddaHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaSftpHelper pddaSftpHelper, PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository, 
        XhbCaseRepository caseRepository, XhbHearingRepository hearingRepository,
        XhbSittingRepository sittingRepository, XhbScheduledHearingRepository scheduledHearingRepository) {
        super(entityManager, xhbConfigPropRepository, environment);
        this.pddaSftpHelper = pddaSftpHelper;
        this.pddaMessageHelper = pddaMessageHelper;
        this.clobRepository = clobRepository;
        this.courtRepository = courtRepository;
        this.caseRepository = caseRepository;
        this.hearingRepository = hearingRepository;
        this.sittingRepository = sittingRepository;
        this.scheduledHearingRepository = scheduledHearingRepository;
    }

    protected XhibitPddaHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository,
        XhbCourtRoomRepository courtRoomRepository, XhbCourtSiteRepository courtSiteRepository,
        XhbCaseRepository caseRepository, XhbHearingRepository hearingRepository,
        XhbSittingRepository sittingRepository, XhbScheduledHearingRepository scheduledHearingRepository) {

        super(entityManager, xhbConfigPropRepository, environment);
        this.pddaMessageHelper = pddaMessageHelper;
        this.clobRepository = clobRepository;
        this.courtRepository = courtRepository;
        this.courtRoomRepository = courtRoomRepository;
        this.courtSiteRepository = courtSiteRepository;
        this.caseRepository = caseRepository;
        this.hearingRepository = hearingRepository;
        this.sittingRepository = sittingRepository;
        this.scheduledHearingRepository = scheduledHearingRepository;
    }

    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        clobRepository = null;
        courtRepository = null;
    }

    /**
     * Sends a public display event.

     * @param event Public display event
     */
    public void sendMessage(PublicDisplayEvent event) {
        LOG.debug("sendMessage({})", event);
        getPublicDisplayNotifier().sendMessage(event);
    }

    private PublicDisplayNotifier getPublicDisplayNotifier() {
        if (publicDisplayNotifier == null) {
            publicDisplayNotifier = new PublicDisplayNotifier();
        }
        return publicDisplayNotifier;
    }

    protected XhbCaseRepository getCaseRepository() {
        if (caseRepository == null || !isEntityManagerActive()) {
            caseRepository = new XhbCaseRepository(entityManager);
        }
        return caseRepository;
    }
    
    protected XhbClobRepository getClobRepository() {
        if (clobRepository == null || !isEntityManagerActive()) {
            clobRepository = new XhbClobRepository(entityManager);
        }
        return clobRepository;
    }

    public XhbCourtRepository getCourtRepository() {
        if (courtRepository == null || !isEntityManagerActive()) {
            courtRepository = new XhbCourtRepository(getEntityManager());
        }
        return courtRepository;
    }
    
    protected XhbCourtRoomRepository getCourtRoomRepository() {
        if (courtRoomRepository == null || !isEntityManagerActive()) {
            courtRoomRepository = new XhbCourtRoomRepository(getEntityManager());
        }
        return courtRoomRepository;
    }
    
    protected XhbCourtSiteRepository getCourtSiteRepository() {
        if (courtSiteRepository == null || !isEntityManagerActive()) {
            courtSiteRepository = new XhbCourtSiteRepository(getEntityManager());
        }
        return courtSiteRepository;
    }
    
    protected XhbHearingRepository getHearingRepository() {
        if (hearingRepository == null || !isEntityManagerActive()) {
            hearingRepository = new XhbHearingRepository(getEntityManager());
        }
        return hearingRepository;
    }
    
    protected XhbScheduledHearingRepository getScheduledHearingRepository() {
        if (scheduledHearingRepository == null || !isEntityManagerActive()) {
            scheduledHearingRepository = new XhbScheduledHearingRepository(getEntityManager());
        }
        return scheduledHearingRepository;
    }
    
    protected XhbSittingRepository getSittingRepository() {
        if (sittingRepository == null || !isEntityManagerActive()) {
            sittingRepository = new XhbSittingRepository(getEntityManager());
        }
        return sittingRepository;
    }

    protected PddaMessageHelper getPddaMessageHelper() {
        if (pddaMessageHelper == null) {
            pddaMessageHelper = new PddaMessageHelper(getEntityManager());
        }
        return pddaMessageHelper;
    }

    protected CppStagingInboundHelper getCppStagingInboundHelper() {
        if (cppStagingInboundHelper == null) {
            cppStagingInboundHelper = new CppStagingInboundHelper();
        }
        return cppStagingInboundHelper;
    }

    protected PddaSftpHelper getPddaSftpHelper() {
        if (pddaSftpHelper == null) {
            pddaSftpHelper = new PddaSftpHelper();
        }
        return pddaSftpHelper;
    }

    protected SftpConfigHelper getSftpConfigHelper() {
        if (sftpConfigHelper == null) {
            sftpConfigHelper = new SftpConfigHelper(entityManager);
        }
        return sftpConfigHelper;
    }

    protected SftpHelperUtil getSftpHelperUtil() {
        if (sftpHelperUtil == null) {
            sftpHelperUtil = new SftpHelperUtil(entityManager);
        }
        return sftpHelperUtil;
    }

    protected PddaSftpHelperSshj getPddaSftpHelperSshj() {
        if (pddaSftpHelperSshj == null) {
            pddaSftpHelperSshj = new PddaSftpHelperSshj();
        }
        return pddaSftpHelperSshj;
    }

    public void setPublicDisplayNotifier(PublicDisplayNotifier publicDisplayNotifier) {
        this.publicDisplayNotifier = publicDisplayNotifier;
    }
    
    /**
     * The data in the PublicDisplayEvent needs to be altered when passed from XHIBIT,
     * as the case numbers, court IDs and courtroom IDs could be different in PDDA.
     */
    //public PublicDisplayEvent translateEvent(PublicDisplayEvent inEvent) {
    //    return event;
    // }
}
