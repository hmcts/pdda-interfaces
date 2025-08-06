package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddadlnotifier.XhbPddaDlNotifierRepository;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**

 * Title: PDDADlNotifierHelper.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Mark Harris
 * @version 1.0
 */
@SuppressWarnings("PMD.NullAssignment")
public class PddaDlNotifierHelper extends PddaConfigHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PddaDlNotifierHelper.class);

    private static final String CONFIG_DL_NOTIFIER_EXECUTION_TIME = "DL_NOTIFIER_EXECUTION_TIME";    
    private static final String YES = "Y";
    private static final String STR_DATEFORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern(STR_DATEFORMAT);
    private static final DateTimeFormatter EXECUTIONTIMEFORMAT =
        DateTimeFormatter.ofPattern(STR_DATEFORMAT + "HH:mm");

    protected XhbPddaDlNotifierRepository pddaDlNotifierRepository;
    protected XhbCourtRepository courtRepository;

    public PddaDlNotifierHelper(EntityManager entityManager) {
        super(entityManager, InitializationService.getInstance().getEnvironment());
    }

    // Junit constructor
    public PddaDlNotifierHelper(EntityManager entityManager, XhbConfigPropRepository xhbConfigPropRepository,
        Environment environment) {
        super(entityManager, xhbConfigPropRepository, environment);
    }
    
    @Override
    protected void clearRepositories() {
        super.clearRepositories();
        pddaDlNotifierRepository = null;
        courtRepository = null;
    }


    public boolean isDailyNotifierRequired() {
        return isTimeToExecute() && isSendToPddaOnly();
    }

    private boolean isTimeToExecute() {
        LocalDateTime executionDateTime = getExecutionTime();
        LocalDateTime timeNow = LocalDateTime.now();
        return executionDateTime != null && timeNow.isAfter(executionDateTime);
    }

    private LocalDateTime getExecutionTime() {
        try {
            String propertyValue = getConfigValue(CONFIG_DL_NOTIFIER_EXECUTION_TIME);
            String stringDate = LocalDateTime.now().format(DATEFORMAT) + propertyValue;
            LocalDateTime date = LocalDateTime.parse(stringDate, EXECUTIONTIMEFORMAT);
            LOG.error(CONFIG_DL_NOTIFIER_EXECUTION_TIME + " = " + propertyValue);
            return date;
        } catch (Exception ex) {
            LOG.error(CONFIG_DL_NOTIFIER_EXECUTION_TIME + " contains invalid time");
            return null;
        }
    }

    /**
     * Send messages to PDDA Only (future way).
     */
    private boolean isSendToPddaOnly() {
        return "1".equals(getPddaSwitcher());
    }

    public void runDailyListNotifier() {
        // Loop through all the active courts
        List<XhbCourtDao> allCourts = getCourtRepository().findAllSafe();
        if (allCourts != null) {
            for (XhbCourtDao court : allCourts) {
                if (!YES.equals(court.getObsInd())) {
                    runDailyListNotifier(court.getCourtId());
                }
            }
        }
    }

    /*
     * Notify the public display for this court that there is a new days list.
     */
    private void runDailyListNotifier(Integer courtId) {
        methodName = "runDailyListNotifier(" + courtId + ")";
        LOG.debug(methodName + " called");
        LocalDateTime lastRunDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

        // Get the record for the court and date
        XhbPddaDlNotifierDao dao = findByCourtAndLastRunDate(courtId, lastRunDate);

        // Check there isn't already a record written for today
        if (dao.getPddaDlNotifierId() == null) {

            // Set the status as SUCCESS
            DlNotifierStatusEnum successEnum = DlNotifierStatusEnum.SUCCESS;
            String status = successEnum.getStatus();
            String errorMessage = null;
            
            try {
                setStatus(dao, status, null);
            } catch (Exception ex) {
                DlNotifierStatusEnum failureEnum = DlNotifierStatusEnum.FAILURE;
                status = failureEnum.getStatus();
                errorMessage = ex.getMessage();
            } finally {
                // Refetch the record ready for updating
                dao = findByCourtAndLastRunDate(courtId, lastRunDate);
                setStatus(dao, status, errorMessage);
            }
        }
    }

    public void setStatus(XhbPddaDlNotifierDao dao, String status, String errorMessage) {
        dao.setStatus(status);
        dao.setErrorMessage(errorMessage);
        Optional<XhbPddaDlNotifierDao> savedDao = getPddaDlNotifierRepository().update(dao);
        if (!savedDao.isEmpty()) {
            LOG.debug("PddaDlNotifier saved");
        }
    }

    private XhbPddaDlNotifierDao findByCourtAndLastRunDate(Integer courtId,
        LocalDateTime lastRunDate) {
        XhbPddaDlNotifierDao dao;
        List<XhbPddaDlNotifierDao> daoList =
            getPddaDlNotifierRepository().findByCourtAndLastRunDateSafe(courtId, lastRunDate);
        if (daoList.isEmpty()) {
            dao = new XhbPddaDlNotifierDao();
            dao.setCourtId(courtId);
            dao.setLastRunDate(lastRunDate);
        } else {
            dao = daoList.get(0);
        }
        return dao;
    }


    protected XhbPddaDlNotifierRepository getPddaDlNotifierRepository() {
        if (pddaDlNotifierRepository == null || !isEntityManagerActive()) {
            pddaDlNotifierRepository = new XhbPddaDlNotifierRepository(getEntityManager());
        }
        return pddaDlNotifierRepository;
    }

    protected XhbCourtRepository getCourtRepository() {
        if (courtRepository == null || !isEntityManagerActive()) {
            courtRepository = new XhbCourtRepository(getEntityManager());
        }
        return courtRepository;
    }
}
