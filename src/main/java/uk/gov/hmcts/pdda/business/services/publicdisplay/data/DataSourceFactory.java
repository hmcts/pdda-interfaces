package uk.gov.hmcts.pdda.business.services.publicdisplay.data;

import jakarta.persistence.EntityManager;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl.GenericPublicDisplayDataSource;
import uk.gov.hmcts.pdda.business.services.publicdisplay.data.impl.SingleCourtRoomDataSource;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.AllCaseStatusQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.AllCaseStatusUnassignedCasesQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.AllCourtStatusQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.CourtDetailQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.CourtListQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.JuryStatusDailyListQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.JuryStatusDailyListUnassignedCasesQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.PublicDisplayQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.SummaryByNameQuery;
import uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query.SummaryByNameUnassignedCasesQuery;
import uk.gov.hmcts.pdda.common.publicdisplay.data.DataSource;
import uk.gov.hmcts.pdda.common.publicdisplay.data.exceptions.NoSuchDataSourceException;
import uk.gov.hmcts.pdda.common.publicdisplay.exceptions.PublicDisplayFailureException;
import uk.gov.hmcts.pdda.common.publicdisplay.types.document.DisplayDocumentType;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayDocumentUri;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p/>
 * Title: A factory for DataSource objects.
 * </p>
 * <p/>
 * <p/>
 * Description: Use this factory to obtain an instance of a DataSource on the
 * uk.gov.hmcts.pdda.business.services.publicdisplay.
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.11 $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public final class DataSourceFactory {
    /**
     * This is the hashmap we use to perform our query class lookups from. It is initialized from
     * the TUPLES array.
     */
    private static final Map<Object, Object> QUERYLOOKUP;

    /**
     * These tuple hold all the document type and query class pairs. We then use the query class to
     * initialize the data source.
     */
    private static final DataSourceFactory.Tuple[] TUPLES = {
        new DataSourceFactory.Tuple(DisplayDocumentType.ALL_COURT_STATUS, AllCourtStatusQuery.class,
            GenericPublicDisplayDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.ALL_COURT_STATUS, AllCourtStatusQuery.class,
            GenericPublicDisplayDataSource.class, Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.COURT_DETAIL, CourtDetailQuery.class,
            SingleCourtRoomDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.COURT_DETAIL, CourtDetailQuery.class,
            SingleCourtRoomDataSource.class, Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.COURT_LIST, CourtListQuery.class,
            SingleCourtRoomDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.COURT_LIST, CourtListQuery.class,
            SingleCourtRoomDataSource.class, Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.DAILY_LIST, JuryStatusDailyListQuery.class,
            GenericPublicDisplayDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.DAILY_LIST,
            JuryStatusDailyListUnassignedCasesQuery.class, GenericPublicDisplayDataSource.class,
            Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.JURY_CURRENT_STATUS,
            JuryStatusDailyListQuery.class, GenericPublicDisplayDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.JURY_CURRENT_STATUS,
            JuryStatusDailyListUnassignedCasesQuery.class, GenericPublicDisplayDataSource.class,
            Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.SUMMARY_BY_NAME, SummaryByNameQuery.class,
            GenericPublicDisplayDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.SUMMARY_BY_NAME,
            SummaryByNameUnassignedCasesQuery.class, GenericPublicDisplayDataSource.class,
            Boolean.TRUE),
        new DataSourceFactory.Tuple(DisplayDocumentType.ALL_CASE_STATUS, AllCaseStatusQuery.class,
            GenericPublicDisplayDataSource.class, Boolean.FALSE),
        new DataSourceFactory.Tuple(DisplayDocumentType.ALL_CASE_STATUS,
            AllCaseStatusUnassignedCasesQuery.class, GenericPublicDisplayDataSource.class,
            Boolean.TRUE)};

    /**
     * Initialize our queryLookup map.
     */
    static {
        QUERYLOOKUP = new ConcurrentHashMap<>();

        for (DataSourceFactory.Tuple source : TUPLES) {
            QUERYLOOKUP.put(getKeyFromSource(source), source);
        }
    }
    
    protected DataSourceFactory() {
        // Protected constructor
    }
    
    private static String getKeyFromSource(DataSourceFactory.Tuple source) {
        StringBuilder sb = new StringBuilder();
        sb.append(source.doc).append(source.unassignedCases);
        return sb.toString();
    }

    private static String getKeyFromUri(DisplayDocumentUri uri) {
        StringBuilder sb = new StringBuilder();
        sb.append(uri.getSimpleDocumentType()).append(uri.isUnassignedRequired());
        return sb.toString();
    }
    
    /**
     * Returns a DataSource for the document specified by the uri.
     * 
     * @param uri the uri of the document we need the data for.
     * 
     * @return a DataSource.
     * 
     * @post return != null
     * @pre uri != null
     * @pre uri.getDocumentType() != null
     */
    public static DataSource getDataSource(EntityManager entityManager, DisplayDocumentUri uri) {
        DataSourceFactory.Tuple tuple = (DataSourceFactory.Tuple) QUERYLOOKUP
            .get(getKeyFromUri(uri));

        if (tuple == null) {
            throw new NoSuchDataSourceException(
                uri.getDocumentType());
        }

        GenericPublicDisplayDataSource dataSource;
        try {
            Class<?> clazz = tuple.queryClass;
            Object queryClass =
                clazz.getConstructor(EntityManager.class).newInstance(entityManager);
            Class<?>[] paramTypes =
                {DisplayDocumentUri.class, PublicDisplayQuery.class};
            Object[] params = {uri, queryClass};
            // Sorry this looks nasty but here is a quick explanation
            // We construct the dataSource specified by
            // tuple.dataSourceClass
            // passing it the uri and the query object specified in
            // tuple.dataSourceClass
            Class<?> dataSourceClass = tuple.dataSourceClass;
            dataSource = (GenericPublicDisplayDataSource) dataSourceClass
                .getConstructor(paramTypes).newInstance(params);
        } catch (Exception e) {
            throw new PublicDisplayFailureException(e);
        }

        return dataSource;
    }

    /**
     * A simple convenience class that pairs up a query class with a display document type.
     */
    private static class Tuple {
        public final Class<?> queryClass;

        public final Class<?> dataSourceClass;

        public final DisplayDocumentType doc;

        public final Boolean unassignedCases;

        public Tuple(DisplayDocumentType doc, Class<?> queryClass, Class<?> dataSourceClass,
            Boolean unassignedCases) {
            this.doc = doc;
            this.queryClass = queryClass;
            this.dataSourceClass = dataSourceClass;
            this.unassignedCases = unassignedCases;
        }
    }
}
