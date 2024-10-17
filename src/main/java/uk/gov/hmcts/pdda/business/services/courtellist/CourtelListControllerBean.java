package uk.gov.hmcts.pdda.business.services.courtellist;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.framework.scheduler.RemoteTask;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.services.pdda.CourtelHelper;

import java.util.List;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class CourtelListControllerBean extends AbstractControllerBean implements RemoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(CourtelListControllerBean.class);

    private static final String METHOD_END = ") - ";
    private static final String ENTERED = " : entered";

    private CourtelHelper courtelHelper;
    private BlobHelper blobHelper;

    public CourtelListControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public CourtelListControllerBean() {
        super();
    }

    /**
     * Implementation of RemoteTask so that this process is called by the timer process.
     * 
     */
    @Override
    public void doTask() {
        processMessages();
    }

    /**
     * Processes messages from Courtel.
     * 
     */
    public void processMessages() {
        String methodName = "processMessages(" + METHOD_END;
        LOG.debug(methodName + ENTERED);
        List<XhbCourtelListDao> xhbCourtelList = getCourtelHelper().getCourtelList();

        if (!xhbCourtelList.isEmpty()) {
            xhbCourtelList.forEach(xhbCourtelListDao -> getCourtelHelper().sendCourtelList(xhbCourtelListDao));
        }
    }

    private CourtelHelper getCourtelHelper() {
        if (courtelHelper == null) {
            courtelHelper = new CourtelHelper(getXhbClobRepository(), getXhbCourtelListRepository(),
                getXhbXmlDocumentRepository(), getBlobHelper(), getXhbConfigPropRepository());
        }
        return courtelHelper;
    }

    private BlobHelper getBlobHelper() {
        if (blobHelper == null) {
            blobHelper = new BlobHelper(getXhbBlobRepository());
        }
        return blobHelper;
    }
}
