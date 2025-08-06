package uk.gov.hmcts.pdda.business.services.publicdisplay.data.ejb;

import jakarta.ejb.ApplicationException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.pdda.business.AbstractControllerBean;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.DataSourceFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.data.DataSource;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.io.Serializable;

@Stateless
@Service
@Transactional
@LocalBean
@ApplicationException(rollback = true)
public class PdDataControllerBean extends AbstractControllerBean implements Serializable {

    private static final long serialVersionUID = -1482124759093214736L;

    private static final Logger LOG = LoggerFactory.getLogger(PdDataControllerBean.class);

    private static final String ENTERED = " : entered";

    private DataSource testDataSource;


    public PdDataControllerBean(EntityManager entityManager) {
        super(entityManager);
    }

    public PdDataControllerBean() {
        super();
    }

    // Unit test constructor
    public PdDataControllerBean(EntityManager entityManager, DataSource testDataSource) {
        super(entityManager);
        this.testDataSource = testDataSource;
    }

    /**
     * Retrieve data for a given document.

     * @param uri the URi of the document to obtain data for.
     * @return the Data.
     */
    public Data getData(DisplayDocumentUri uri) {
        String methodName = "getData(" + uri + ") - ";
        LOG.debug("{}{}", methodName, ENTERED);

        DataSource dataSource = createDataSource(uri);
        dataSource.retrieve(getEntityManager());
        return dataSource.getData();
    }

    private DataSource createDataSource(DisplayDocumentUri uri) {
        // Used for unit tests
        if (testDataSource != null) {
            return testDataSource;
        }
        // Live version
        return DataSourceFactory.getDataSource(getEntityManager(), uri);
    }

}
