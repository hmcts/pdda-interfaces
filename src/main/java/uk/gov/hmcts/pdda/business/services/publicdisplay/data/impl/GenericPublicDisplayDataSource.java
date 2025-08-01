
package uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl;

import jakarta.ejb.FinderException;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.CppDataSourceFactory;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.cpptoxhibit.AbstractCppToPublicDisplay;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.PublicDisplayQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.data.DataSource;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;
import uk.gov.hmcts.pdda.common.publicdisplay.util.StringUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import javax.xml.xpath.XPathExpressionException;

/**
 * <p/>
 * Title: A general purpose DataSource.

 * <p/>
 * <p/>
 * Description: A general purpose DataSource.

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.15 $
 */
public class GenericPublicDisplayDataSource extends DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(GenericPublicDisplayDataSource.class);

    private PublicDisplayQuery query;

    private XhbCourtRepository xhbCourtRepository;

    /**
     * Creates a new GenericPublicDisplayDataSource object.

     * @param uri the uri of the document we wish to obtain data for.
     * @param query the query we will use to obtain the data, this is provided by the
     *        DataSourceFactory

     * @pre uri != null
     * @pre query != null
     * @post this.query != null
     * @post getUri() != null
     * @see uk.gov.hmcts.pdda.business.services.publicdisplay.data.DataSourceFactory
     */
    public GenericPublicDisplayDataSource(DisplayDocumentUri uri, PublicDisplayQuery query) {
        super(uri);
        this.query = query;
    }

    /**
     * JUnit constructor.
     */
    public GenericPublicDisplayDataSource(DisplayDocumentUri uri, PublicDisplayQuery query,
        XhbCourtRepository xhbCourtRepository) {
        this(uri, query);
        this.xhbCourtRepository = xhbCourtRepository;
    }

    /**
     * If there is any data for the query place it into the data object otherwise leave it empty.

     * @param entityManager EntityManager

     * @pre getUri() != null
     * @pre getData() != null
     */
    @Override
    @SuppressWarnings("unchecked")
    public void retrieve(final EntityManager entityManager) {
        LOG.info("retrieve()");
        reset();
        LocalDateTime now = LocalDateTime.now();
        Date date = new Date();

        int courtId = getUri().getCourtId();

        // Passing in DocumentType language and country
        Collection<?> summaryByNameData =
            query.getData(now, courtId, getUri().getCourtRoomIdsWithoutUnassigned());

        // Retrieve the CPP data if required
        AbstractCppToPublicDisplay cppDataSource =
            CppDataSourceFactory.getDataSource(getUri().getDocumentType().getShortName(), date,
                courtId, getUri().getCourtRoomIdsWithoutUnassigned());
        if (cppDataSource != null) {
            try {
                // Add the CPP data to the Xhibit data
                summaryByNameData.addAll(cppDataSource.getCppData(entityManager));

                // Post CPP Processing e.g. sorting and duplicate removal
                Collection<?> processedData = CppDataSourceFactory
                    .postProcessing(getUri().getDocumentType().getShortName(), summaryByNameData);
                setData(processedData);
            } catch (XPathExpressionException e) {
                // XPath Error adding the CPP Data so log the error and just use the Xhibit data
                LOG.error("retrieve() - XPathExpressionException parsing CPP XML - {}",
                    e.getMessage());
                setData(summaryByNameData);
            } catch (Exception e) {
                // Other Exception adding the CPP Data so log the error and just use the Xhibit
                // data
                LOG.error("retrieve() - Exception retrieving CPP Data - {}, {}",
                    cppDataSource.getClass().getName(), e.getMessage());
                setData(summaryByNameData);
            }
        } else {
            DisplayDocumentType displayDocumentType =
                getUri() != null ? getUri().getDocumentType() : null;
            String shortName =
                displayDocumentType != null ? displayDocumentType.getShortName() : null;
            String dateStr = date.toString();
            // no CPP data to retrieve, just use the Xhibit data
            LOG.info(
                "retrieve() - no CPP data to retrieve - court short name:{} - date:{} - court id:{}",
                shortName, dateStr, courtId);
            setData(summaryByNameData);
        }

        setCourtName(courtId, entityManager);

        LOG.info("Finished retrieve()");
    }

    /**
     * Commonly used setData method for setting the public display data.

     * @param data Collection
     */
    private void setData(Collection<?> data) {
        if (data != null && !data.isEmpty()) {
            getData().setTable(data);
        } else {
            getData().setTable(new ArrayList<>());
        }
    }

    /**
     * Sets the court name in the data object for the court that we're retrieving data for.

     * @param courtId Integer

     * @throws FinderException Exception
     */
    private void setCourtName(final Integer courtId, final EntityManager entityManager) {
        Optional<XhbCourtDao> court = getXhbCourtRepository(entityManager).findByIdSafe(courtId);
        if (!court.isPresent()) {
            throw new CourtNotFoundException(courtId);
        }
        getData().setCourtName(StringUtilities.toSentenceCase(court.get().getCourtName()));
    }

    private XhbCourtRepository getXhbCourtRepository(EntityManager entityManager) {
        if (xhbCourtRepository == null) {
            return new XhbCourtRepository(entityManager);
        }
        return xhbCourtRepository;
    }
}
