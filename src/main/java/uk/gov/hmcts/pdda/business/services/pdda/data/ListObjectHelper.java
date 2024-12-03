package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * Title: ListObjectHelper.
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
 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList"})
public class ListObjectHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ListObjectHelper.class);
    private static final String COURTHOUSECODE = "cs:CourtHouseCode";
    private static final String COURTHOUSENAME = "cs:CourtHouseName";
    private static final String[] COURTSITE_NODES = {COURTHOUSECODE, COURTHOUSENAME};
    

    private final DataHelper dataHelper = new DataHelper();
    private Optional<XhbCourtSiteDao> xhbCourtSiteDao;

    public void validateNodeMap(Map<String, String> nodesMap, String lastEntryName) {
        if (Arrays.asList(COURTSITE_NODES).contains(lastEntryName)) {
            xhbCourtSiteDao = validateCourtSite(nodesMap);
        }
    }

    public Optional<XhbCourtSiteDao> validateCourtSite(Map<String, String> nodesMap) {
        LOG.info("validateCourtSite()");
        String courtHouseName = nodesMap.get(COURTHOUSENAME);
        String courtHouseCode = nodesMap.get(COURTHOUSECODE);
        if (courtHouseName != null && courtHouseCode != null) {
            return dataHelper.validateCourtSite(courtHouseName, courtHouseCode);
        }
        return Optional.empty();
    }
}
