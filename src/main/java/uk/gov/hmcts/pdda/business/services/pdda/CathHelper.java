package uk.gov.hmcts.pdda.business.services.pdda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.CourtelJson;
import uk.gov.hmcts.pdda.business.entities.xhbcourtellist.XhbCourtelListDao;

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

    public XhbCourtelListDao populateCourtelListBlob(XhbCourtelListDao xhbCourtelListDao) {
        xhbCourtelListDao.setBlob(blobHelper.getBlob(xhbCourtelListDao.getBlobId()));
        return xhbCourtelListDao;
    }

    public String generateJsonString(XhbCourtelListDao xhbCourtelListDao, CourtelJson courtelJson) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            courtelJson.setJson(mapper.writeValueAsString(xhbCourtelListDao));
        } catch (JsonProcessingException e) {
            LOG.error("Error creating JSON String for {} object.", xhbCourtelListDao);
        }
        return courtelJson.getJson();
    }

    public void send(String jsonString) {
        LOG.debug("send({})", jsonString);
        // TODO PDDA-364
    }
}
