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
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpConfigHelper;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpHelperUtil;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

/**
 * <p>
 * Title: Xhibit PDDAHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 * @version 1.0
 */
public abstract class XhibitPddaHelper extends PddaConfigHelper {
    private static final Logger LOG = LoggerFactory.getLogger(XhibitPddaHelper.class);

    private PublicDisplayNotifier publicDisplayNotifier;
    private XhbCourtRepository courtRepository;
    private XhbClobRepository clobRepository;
    private PddaMessageHelper pddaMessageHelper;
    private CppStagingInboundHelper cppStagingInboundHelper;
    private PddaSftpHelper sftpHelper;
    private SftpConfigHelper sftpConfigHelper;
    private SftpHelperUtil sftpHelperUtil;

    protected XhibitPddaHelper(EntityManager entityManager, Environment environment) {
        super(entityManager, environment);
    }

    // Junit constructor
    protected XhibitPddaHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository, Environment environment,
        PddaSftpHelper sftpHelper, PddaMessageHelper pddaMessageHelper,
        XhbClobRepository clobRepository, XhbCourtRepository courtRepository) {
        super(entityManager, xhbConfigPropRepository, environment);
        this.sftpHelper = sftpHelper;
        this.pddaMessageHelper = pddaMessageHelper;
        this.clobRepository = clobRepository;
        this.courtRepository = courtRepository;
    }

    /**
     * Sends a public display event.
     * 
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
        if (clobRepository == null) {
            clobRepository = new XhbClobRepository(entityManager);
        }
        return clobRepository;
    }

    protected XhbCourtRepository getCourtRepository() {
        if (courtRepository == null) {
            courtRepository = new XhbCourtRepository(entityManager);
        }
        return courtRepository;
    }

    protected PddaMessageHelper getPddaMessageHelper() {
        if (pddaMessageHelper == null) {
            pddaMessageHelper = new PddaMessageHelper(entityManager);
        }
        return pddaMessageHelper;
    }

    protected CppStagingInboundHelper getCppStagingInboundHelper() {
        if (cppStagingInboundHelper == null) {
            cppStagingInboundHelper = new CppStagingInboundHelper();
        }
        return cppStagingInboundHelper;
    }

    protected PddaSftpHelper getSftpHelper() {
        if (sftpHelper == null) {
            sftpHelper = new PddaSftpHelper();
        }
        return sftpHelper;
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
}
