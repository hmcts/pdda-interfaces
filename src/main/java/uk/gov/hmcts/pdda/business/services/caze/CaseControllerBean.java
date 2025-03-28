package uk.gov.hmcts.pdda.business.services.caze;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;
import uk.gov.hmcts.pdda.business.vos.services.caze.ScheduledHearingValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CaseControllerBean extends AbstractControllerBean {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -1482124779093244736L;

    private static final Logger LOG = LoggerFactory.getLogger(CaseControllerBean.class);

    private XhbHearingRepository xhbHearingRepository;

    private XhbHearingListRepository xhbHearingListRepository;

    public CaseControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public CaseControllerBean() {
        super();
    }
    
    protected void clearRepositories() {
        super.clearRepositories();
        xhbHearingRepository = null;
        xhbHearingListRepository = null;
    }

    /**
     * This method is called to get all the hearings scheduled for a case on a particular day.
     * 
     * @param caseId The case for which to retrieve the scheduled hearings.
     * @param day The Calendar object representing the day to look for scheduled hearings on.
     * @return an array of ScheduledHearingValues representing the scheduled hearings occurring for
     *         the case on the day in question.
     * @throws CaseControllerException if there is a problem retrieving the scheduled hearings.
     */
    public ScheduledHearingValue[] getScheduledHearingsForCaseOnDay(Integer caseId, Calendar day) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("START: getScheduledHearingsForCaseOnDay(caseId={}, day={})", caseId, day);
        }
        List<ScheduledHearingValue> returnList = new ArrayList<>();
        // find all the scheduled hearings for this case
        List<ScheduledHearingValue> schedHearings = getScheduledHearings(caseId);

        Iterator<ScheduledHearingValue> schedHearingsValues = schedHearings.iterator();
        while (schedHearingsValues.hasNext()) {
            ScheduledHearingValue schedHearingValue = schedHearingsValues.next();
            LocalDateTime hearingDate = schedHearingValue.getScheduledHearingDate();
            // if the sched hearing is on this date then add to list.
            if (hearingDate != null && day.get(Calendar.DAY_OF_YEAR) == hearingDate.getDayOfYear()
                && day.get(Calendar.YEAR) == hearingDate.getYear()) {
                returnList.add(schedHearingValue);
            }
        }
        ScheduledHearingValue[] returnArray = new ScheduledHearingValue[returnList.size()];
        returnList.toArray(returnArray);
        return returnArray;
    }

    /**
     * Returns all the hearings which have been scheduled so far for a given caseId.
     * 
     * @param caseID The case id
     * @return A Collection of ScheduledHearing value objects containing the scheduled hearing id
     *         and date.
     * @throws CaseControllerException Exception
     */
    private List<ScheduledHearingValue> getScheduledHearings(Integer caseID) {
        LOG.debug("START: getScheduledHearings(caseID=" + caseID + ")");
        List<ScheduledHearingValue> scheduledHearingValues = new ArrayList<>();

        List<XhbHearingDao> hearingBeans =
            getXhbHearingRepository().findByCaseId(caseID);
        LOG.debug("found " + hearingBeans.size() + " hearings for caseId=" + caseID);
        Iterator<XhbHearingDao> hearingsIterator;
        hearingsIterator = hearingBeans.iterator();

        // get scheduled hearings for each hearing
        while (hearingsIterator.hasNext()) {
            XhbHearingDao hearing = hearingsIterator.next();
            List<XhbScheduledHearingDao> scheduledHearingBeans =
                (List<XhbScheduledHearingDao>) hearing.getScheduledHearings();
            LOG.debug("found " + scheduledHearingBeans.size() + " scheduled hearings for hearing="
                + hearing);

            // get the ID and date for each scheduled hearing
            Iterator<XhbScheduledHearingDao> scheduledHearingsIterator =
                scheduledHearingBeans.iterator();
            while (scheduledHearingsIterator.hasNext()) {
                XhbScheduledHearingDao scheduledHearing = scheduledHearingsIterator.next();
                ScheduledHearingValue scheduledHearingValue = getScheduledHearingValue();
                scheduledHearingValue
                    .setScheduledHearingID(scheduledHearing.getScheduledHearingId());
                scheduledHearingValue.setCaseActive(scheduledHearing.getIsCaseActive());

                LocalDateTime schedHearingDate = null;
                XhbSittingDao sitting = scheduledHearing.getXhbSitting();
                if (sitting.getListId() != null) {
                    Optional<XhbHearingListDao> hearingList =
                        getXhbHearingListRepository().findById(sitting.getListId());
                    if (hearingList.isPresent()) {
                        schedHearingDate = hearingList.get().getStartDate();
                    }
                }
                scheduledHearingValue.setScheduledHearingDate(schedHearingDate);

                LOG.debug("adding scheduled hearing value=" + scheduledHearingValue);
                scheduledHearingValues.add(scheduledHearingValue);
            }
        }

        return scheduledHearingValues;
    }

    private ScheduledHearingValue getScheduledHearingValue() {
        return new ScheduledHearingValue();
    }
    
    /**
     * Returns the xhbHearingRepository object, initialising if currently null.
     * 
     * @return XhbHearingRepository
     */
    private XhbHearingRepository getXhbHearingRepository() {
        if (xhbHearingRepository == null  || !isEntityManagerActive()) {
            xhbHearingRepository = new XhbHearingRepository(getEntityManager());
        }
        return xhbHearingRepository;
    }

    /**
     * Returns the xhbHearingListRepository object, initialising if currently null.
     * 
     * @return XhbHearingListRepository
     */
    private XhbHearingListRepository getXhbHearingListRepository() {
        if (xhbHearingListRepository == null  || !isEntityManagerActive()) {
            xhbHearingListRepository = new XhbHearingListRepository(getEntityManager());
        }
        return xhbHearingListRepository;
    }
}
