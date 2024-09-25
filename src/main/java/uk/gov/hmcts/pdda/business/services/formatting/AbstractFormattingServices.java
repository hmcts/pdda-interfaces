package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;

import java.util.List;
import java.util.Optional;

/**
 * AbstractFormattingServices.
 */

public class AbstractFormattingServices extends AbstractFormattingRepositories {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFormattingServices.class);
    private static final String FD = "FD";
    private static final String NF = "NF";

    protected final BlobHelper blobHelper;
    
    public AbstractFormattingServices(EntityManager entityManager, BlobHelper blobHelper) {
        super(entityManager);
        this.blobHelper = blobHelper;
    }

    protected Long createBlob(byte[] blobData) {
        LOG.debug("createBlob({})", blobData);
        return blobHelper.createBlob(blobData);
    }

    protected Optional<XhbFormattingDao> getXhbFormattingDao(FormattingValue formattingValue) {
        LOG.debug("getXhbFormattingDao({})", formattingValue);
        Optional<XhbFormattingDao> bvCpp = getXhbFormattingRepository().findById(formattingValue.getFormattingId());
        if (bvCpp.isPresent()) {
            Long blobId = createBlob(FormattingServiceUtils.getEmptyByteArray());
            XhbFormattingDao xhbFormatting = new XhbFormattingDao();
            xhbFormatting.setFormattingId(0);
            xhbFormatting.setDateIn(bvCpp.get().getDateIn());
            xhbFormatting.setFormatStatus("DR");
            xhbFormatting.setDistributionType(bvCpp.get().getDistributionType());
            xhbFormatting.setMimeType(bvCpp.get().getMimeType());
            xhbFormatting.setDocumentType(bvCpp.get().getDocumentType());
            xhbFormatting.setCourtId(bvCpp.get().getCourtId());
            xhbFormatting.setFormattedDocumentBlobId(blobId);
            xhbFormatting.setXmlDocumentClobId(Long.valueOf(0));
            xhbFormatting.setLanguage(bvCpp.get().getLanguage());
            xhbFormatting.setCountry(bvCpp.get().getCountry());
            xhbFormatting.setMajorSchemaVersion(bvCpp.get().getMajorSchemaVersion());
            xhbFormatting.setMinorSchemaVersion(bvCpp.get().getMinorSchemaVersion());
            xhbFormatting.setLastUpdateDate(bvCpp.get().getLastUpdateDate());
            xhbFormatting.setCreationDate(bvCpp.get().getCreationDate());
            xhbFormatting.setLastUpdatedBy(bvCpp.get().getLastUpdatedBy());
            xhbFormatting.setCreatedBy(bvCpp.get().getCreatedBy());
            xhbFormatting.setVersion(bvCpp.get().getVersion());
            return getXhbFormattingRepository().update(xhbFormatting);
        }
        return Optional.empty();
    }

    protected Long getLatestXhibitClobId(final Integer courtId, final String documentType, final String language,
        final String courtSiteName) {
        Long clobId = null;
        List<XhbFormattingDao> list =
            getXhbFormattingRepository().findByDocumentAndClob(courtId, documentType, language, courtSiteName);
        if (!list.isEmpty()) {
            clobId = list.get(0).getXmlDocumentClobId();
        }
        LOG.debug("getLatestXhibitClobId() - {}", clobId);
        return clobId;
    }

    public Optional<XhbClobDao> getClob(final Long clobId) {
        return getXhbClobRepository().findById(clobId);
    }

    protected String getClobData(Long clobId) {
        LOG.debug("getClobData({})", clobId);
        Optional<XhbClobDao> dao = getClob(clobId);
        return dao.isPresent() ? dao.get().getClobData() : null;
    }

    protected Long createClob(final XhbClobDao clobDao) {
        LOG.debug("createCppListClob({})", clobDao);
        Optional<XhbClobDao> result = getXhbClobRepository().update(clobDao);
        return result.isPresent() ? result.get().getClobId() : null;
    }

    protected void updatePostMerge(final XhbCppFormattingMergeDao formattingMergeVal, final String clobData) {
        LOG.debug("updatePostMerge({},{})", formattingMergeVal, clobData);
        // Create new XhbClob record and return the ClobId
        XhbClobDao clobVal = new XhbClobDao();
        clobVal.setClobData(clobData);
        Optional<XhbClobDao> clobDao = getXhbClobRepository().update(clobVal);
        Long clobId = clobDao.isPresent() ? clobDao.get().getClobId() : null;

        // Update the XhbFormatting record with the Clob Id
        Optional<XhbFormattingDao> formattingDao =
            getXhbFormattingRepository().findById(formattingMergeVal.getFormattingId());
        if (formattingDao.isPresent()) {
            XhbFormattingDao formatting = formattingDao.get();
            formatting.setXmlDocumentClobId(clobId);
            getXhbFormattingRepository().update(formatting);
        }

        // Update the XhbCppFormatting record with a blank error message and merge
        // success status
        updateCppFormatting(formattingMergeVal.getCppFormattingId(), "MS", "");

        // Create the XhbCppFormattingMerge record
        getXhbCppFormattingMergeRepository().save(formattingMergeVal);

    }

    /**
     * Update the CppFormatting with the formatStatus and errorMessage.
     * 
     * @param cppFormattingId CPP Formatting Id
     * @param formatStatus Format Status
     * @param errorMessage Error Message
     */
    public void updateCppFormatting(Integer cppFormattingId, String formatStatus, String errorMessage) {
        LOG.debug("updateCppFormatting({},{},{})", cppFormattingId, formatStatus, errorMessage);
        Optional<XhbCppFormattingDao> cppFormattingDao = getXhbCppFormattingRepository().findById(cppFormattingId);
        if (cppFormattingDao.isPresent()) {
            XhbCppFormattingDao cppFormatting = cppFormattingDao.get();
            cppFormatting.setErrorMessage(errorMessage);
            cppFormatting.setFormatStatus(formatStatus);
            getXhbCppFormattingRepository().update(cppFormatting);
        }
    }

    protected XhbCppListDao updateCppList(final XhbCppListDao cppList) {
        cppList.setLastUpdatedBy("XHIBIT");
        LOG.debug("updateCppList({})", cppList);
        Optional<XhbCppListDao> result = getXhbCppListRepository().update(cppList);
        return result.isPresent() ? result.get() : null;
    }


    /**
     * Update the Formatting with the formatStatus and errorMessage.
     * 
     * @param formattingId Formatting Id
     * @param success Success
     */
    public void updateFormattingStatus(Integer formattingId, boolean success) {
        Optional<XhbFormattingDao> formattingDao = getXhbFormattingRepository().findById(formattingId);
        if (formattingDao.isPresent()) {
            XhbFormattingDao formatting = formattingDao.get();

            String formatStatus = null;
            if (success) {
                LOG.debug("DR(Document Ready)");
                formatStatus = "DR";
            } else if (FD.equals(formatting.getFormatStatus())) {
                LOG.debug("FD(Formatting Document) -> FE(Formatting Error)");
                formatStatus = "FE";
            } else if (NF.equals(formatting.getFormatStatus())) {
                LOG.debug("NF(New Formatting) -> FF(Failed Formatting)");
                formatStatus = "FF";
            } else {
                LOG.debug("No formatting status change");
            }

            if (formatStatus != null) {
                formatting.setFormatStatus(formatStatus);
                getXhbFormattingRepository().update(formatting);
            }
        }
    }
}
