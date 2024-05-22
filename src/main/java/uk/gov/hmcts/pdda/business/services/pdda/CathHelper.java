package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListJson;

/**
 * <p>
 * Title: CathHelper.
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
 * @author Luke Gittins
 * @version 1.0
 */
public class CathHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CathHelper.class);

    private final BlobHelper blobHelper;

    public CathHelper(BlobHelper blobHelper) {
        this.blobHelper = blobHelper;
    }

    public XhbCourtelListJson convertDaoToJsonObject(XhbCourtelListDao xhbCourtelListDao) {
        XhbCourtelListJson xhbCourtelListJson = new XhbCourtelListJson();
        xhbCourtelListJson.setBlobData(blobHelper.getBlobData(xhbCourtelListDao.getBlobId()));
        return xhbCourtelListJson;
    }

    public String generateJsonString(XhbCourtelListJson xhbCourtelListJson) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(xhbCourtelListJson);
        } catch (JsonProcessingException e) {
            LOG.error("Error creating JSON String for {} object.", xhbCourtelListJson);
        }
        return json;
    }

    public void send(String jsonString) {
        LOG.debug("send({})", jsonString);
        // TODO PDDA-364
    }
}
