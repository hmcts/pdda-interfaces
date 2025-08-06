package uk.gov.hmcts.pdda.business.services.cpplist;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CppListControllerBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CppListControllerBean.class);
    private static final String ENTERED = " : entered";

    @PersistenceContext
    private EntityManager entityManager;

    private XhbCppListRepository xhbCppListRepository;

    public CppListControllerBean(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public CppListControllerBean() {
        super();
    }

    /**

     * Returns the latest unprocessed XHB_CPP_LIST.

     * @param courtId ID of the court
     * @param listType List Type
     * @param listStartDate List Start Date
     * @return CppListComplexValue

     */
    public XhbCppListDao getLatestCppList(final Integer courtId, final String listType,
        final LocalDateTime listStartDate) {
        String methodName =
            "getLatestCPPList(" + courtId + "," + listType + "," + listStartDate + ") ";
        LOG.debug(methodName + ENTERED);
        XhbCppListDao xcl = null;
        List<XhbCppListDao> lists = getXhbListRepository()
            .findByCourtCodeAndListTypeAndListDateSafe(courtId, listType, listStartDate);
        if (lists != null && !lists.isEmpty()) {
            xcl = lists.get(0);
        }
        return xcl;
    }

    /**

     * Update the XHB_CPP_LIST record.

     * @param xcl XhbCppListDao

     */
    public void updateCppList(XhbCppListDao xcl) {
        String methodName = "updateCppList() ";
        LOG.debug(methodName + ENTERED);


        getXhbListRepository().update(xcl);
    }

    /**
     * checkForExistingCppListRecord.
     * @param courtCode int 
     * @param listType String
     * @param listStartDate LocalDateTime
     * @return XhbCppListDAO

     */
    public XhbCppListDao checkForExistingCppListRecord(Integer courtCode, String listType,
        LocalDateTime listStartDate, LocalDateTime listEndDate) {
        String methodName = "checkForExistingCppListRecord(" + courtCode + "," + listType + ","
            + listStartDate + "," + listEndDate + ") ";
        LOG.debug(methodName + ENTERED);
        XhbCppListDao xcl = null;
        List<XhbCppListDao> lists =
            getXhbListRepository().findByCourtCodeAndListTypeAndListStartDateAndListEndDateSafe(
                courtCode, listType, listStartDate, listEndDate);
        if (lists != null && !lists.isEmpty()) {
            xcl = lists.get(0);
        }
        return xcl;
    }

    private XhbCppListRepository getXhbListRepository() {
        if (xhbCppListRepository == null) {
            xhbCppListRepository = new XhbCppListRepository(entityManager);
        }
        return xhbCppListRepository;
    }

}
