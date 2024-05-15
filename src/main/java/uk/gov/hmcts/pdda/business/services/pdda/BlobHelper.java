package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobDao;
import uk.gov.hmcts.pdda.business.entities.xhbblob.XhbBlobRepository;
import uk.gov.hmcts.pdda.business.services.formatting.FormattingServiceUtils;

import java.util.Optional;

/**
 * <p>
 * Title: BlobHelper.
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
public class BlobHelper {

    private static final Logger LOG = LoggerFactory.getLogger(BlobHelper.class);
    
    private final XhbBlobRepository xhbBlobRepository;
    
    public BlobHelper(XhbBlobRepository xhbBlobRepository) {
        this.xhbBlobRepository = xhbBlobRepository;
    }
    
    public Long createBlob(byte[] blobData) {
        LOG.debug("createBlob({})", blobData);
        XhbBlobDao dao = FormattingServiceUtils.createXhbBlobDao(blobData);
        Optional<XhbBlobDao> savedDao = xhbBlobRepository.update(dao);
        return savedDao.isPresent() ? savedDao.get().getBlobId() : null;
    }
}
