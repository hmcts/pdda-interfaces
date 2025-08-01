package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
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
    private XhbCourtRepository courtRepository;
    private XhbClobRepository clobRepository;
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
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment);
        this.pddaSftpHelper = pddaSftpHelper;
        this.pddaMessageHelper = pddaMessageHelper;
        this.clobRepository = clobRepository;
        this.courtRepository = courtRepository;
    }

    protected XhibitPddaHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository) {

        super(entityManager, xhbConfigPropRepository, environment);
        this.pddaMessageHelper = pddaMessageHelper;
        this.clobRepository = clobRepository;
        this.courtRepository = courtRepository;
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

    protected XhbClobRepository getClobRepository() {
        if (clobRepository == null || !isEntityManagerActive()) {
            clobRepository = new XhbClobRepository(entityManager);
        }
        return clobRepository;
    }

    protected XhbCourtRepository getCourtRepository() {
        if (courtRepository == null || !isEntityManagerActive()) {
            courtRepository = new XhbCourtRepository(getEntityManager());
        }
        return courtRepository;
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
}
