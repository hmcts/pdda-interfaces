package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayRepository;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * <p>
 * Title: ClearDownHelper.
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
 * @author Nathan Toft
 * @version 1.0
 */
public class ClearDownHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ClearDownHelper.class);
    public static final DateTimeFormatter DATETIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    public static final String RESET_DISPLAY_IWP_TIME = "RESET_DISPLAY_IWP_TIME";

    private final EntityManager entityManager;
    private XhbCrLiveDisplayRepository xhbCrLiveDisplayRepository;
    private XhbConfigPropRepository xhbConfigPropRepository;

    public ClearDownHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // JUnit Constructor
    public ClearDownHelper(XhbCrLiveDisplayRepository xhbCrLiveDisplayRepository,
        XhbConfigPropRepository xhbConfigPropRepository) {
        this(null);
        this.xhbCrLiveDisplayRepository = xhbCrLiveDisplayRepository;
        this.xhbConfigPropRepository = xhbConfigPropRepository;
    }

    /**
     * isClearDownRequired.
     */
    public boolean isClearDownRequired() {
        LocalDateTime clearDownTime = getClearDownTime();
        return clearDownTime != null && LocalDateTime.now().isAfter(clearDownTime);
    }

    private LocalDateTime getClearDownTime() {
        List<XhbConfigPropDao> xhbConfigPropDaoList =
            getXhbConfigPropRepository().findByPropertyName(RESET_DISPLAY_IWP_TIME);
        if (!xhbConfigPropDaoList.isEmpty()) {
            String propertyValue = xhbConfigPropDaoList.get(0).getPropertyValue();
            return getClearDownTime(propertyValue);
        }
        return null;
    }

    /**
     * getClearDownTime.
     */
    public LocalDateTime getClearDownTime(String propertyValue) {
        LOG.debug("getclearDownTime({})", propertyValue);
        String todaysDate = DateUtils.getDate(LocalDateTime.now());
        try {
            return LocalDateTime.parse(todaysDate + propertyValue, DATETIME_FORMAT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * resetLiveDisplays.
     */
    public void resetLiveDisplays() {
        LOG.debug("resetLiveDisplays()");
        List<XhbCrLiveDisplayDao> liveDisplays =
            getXhbCrLiveDisplayRepository().findLiveDisplaysWhereStatusNotNull();
        if (!liveDisplays.isEmpty()) {
            LOG.debug("Found {} live displays to be reset", liveDisplays.size());
            for (XhbCrLiveDisplayDao liveDisplay : liveDisplays) {
                resetLiveDisplay(liveDisplay);
            }
        }
    }

    private void resetLiveDisplay(XhbCrLiveDisplayDao liveDisplay) {
        try {
            LOG.debug("Setting display {} status {} to null", liveDisplay.getCrLiveDisplayId(),
                liveDisplay.getStatus());
            liveDisplay.setStatus(null);
            getXhbCrLiveDisplayRepository().update(liveDisplay);
        } catch (Exception ex) {
            LOG.error("Error: {}", ex.getMessage());
        }
    }

    private XhbCrLiveDisplayRepository getXhbCrLiveDisplayRepository() {
        if (xhbCrLiveDisplayRepository == null) {
            xhbCrLiveDisplayRepository = new XhbCrLiveDisplayRepository(entityManager);
        }
        return xhbCrLiveDisplayRepository;
    }

    private XhbConfigPropRepository getXhbConfigPropRepository() {
        if (xhbConfigPropRepository == null) {
            xhbConfigPropRepository = new XhbConfigPropRepository(entityManager);
        }
        return xhbConfigPropRepository;
    }
}
