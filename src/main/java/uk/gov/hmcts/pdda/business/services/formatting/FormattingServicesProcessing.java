package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * FormattingServicesProcessing.
 */

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public abstract class FormattingServicesProcessing extends AbstractFormattingServices {

    private static final Logger LOG = LoggerFactory.getLogger(FormattingServicesProcessing.class);

    private static final String DELIMITER = " : ";
    private static final String IP = "IP";
    protected static final String IWP = "IWP";
    private static final String MERGING_EXCEPTION_STRING = "Error Merging: ";
    protected static final String TRANSFORMATION_ERROR =
        " An error has occured during the transformation process";

    private AbstractXmlMergeUtils xmlUtils;
    private TranslationBundles translationBundles;

    protected FormattingServicesProcessing(EntityManager entityManager, BlobHelper blobHelper) {
        super(entityManager, blobHelper);
    }

    protected void processIwpDocument(final FormattingValue formattingValue,
        final String translationXml)
        throws SAXException, IOException, ParserConfigurationException {
        LOG.debug("formattingValue is a CPP Internet Web Page document and ID is {}",
            formattingValue.getFormattingId());

        try {
            // Use the 'xslt_config.xml' to determine the transformation from xml to html
            Writer buffer = TransformerUtils.transform(getXslServices(), getFormattingConfig(),
                formattingValue, translationXml, new StringWriter());
            LOG.debug("\n\n\n\n\n\n\nprocessIwpDocument({}):\n {}", formattingValue, buffer);
            
            // Update the formattedDocumentBlob with the formatted document
            blobHelper.updateBlob(formattingValue.getFormattedDocumentBlobId(),
                buffer.toString().getBytes());
            
        } catch (TransformerException ex) {
            throw new FormattingException(MERGING_EXCEPTION_STRING
                + formattingValue.getFormattingId() + DELIMITER + ex.getMessage(), ex);
        }
    }

    protected void processListDocument(final FormattingValue formattingValue,
        final String translationXml)
        throws SAXException, IOException, ParserConfigurationException, TransformerException {
        LOG.debug("formattingValue is an List document and ID is {}",
            formattingValue.getFormattingId());
        try {
            XhbCppListDao cppList =
                getXhbCppListRepository().findByClobId(formattingValue.getXmlDocumentClobId());
 
            // Set In progress (if it isn't already at IP - ie a crash)
            if (!IP.equals(cppList.getStatus())) {
                // Clear the previous merge attempts and set in progress
                cppList.setMergedClobId(null);
                cppList.setErrorMessage(null);
                cppList.setStatus("IP");
                cppList = updateCppList(cppList);
                if (cppList == null || cppList.getCppListId() == null) {
                    LOG.error("Failed to save cppList - Status IP");
                    throw new FormattingException(TRANSFORMATION_ERROR);
                }
            }

            // Use the 'xslt_config.xml' to determine the transformation from xml to html
            Writer buffer = TransformerUtils.transform(getXslServices(), getFormattingConfig(),
                formattingValue, translationXml, new StringWriter());
            LOG.debug("\n\n\n\n\n\n\nprocessListDocument({}):\n {}", formattingValue, buffer);

            // Set the status as successful
            Optional<XhbCppListDao> optCppList =
                getXhbCppListRepository().findById(cppList.getCppListId());
            if (optCppList.isEmpty()) {
                LOG.error("Failed to save cppList - Status MS");
                throw new FormattingException(TRANSFORMATION_ERROR);
            }

            // Update the formattedDocumentBlob with the formatted document
            blobHelper.updateBlob(formattingValue.getFormattedDocumentBlobId(),
                buffer.toString().getBytes());

            cppList = optCppList.get();
            cppList.setStatus("MS");
            updateCppList(cppList);

            // We should not get a finder exception as all the values we are searching on
            // should not come back with a finder exception
        } catch (EJBException ex) {
            throw new FormattingException(TRANSFORMATION_ERROR, ex);
        }
    }

    public FormattingValue getFormattingValue(final XhbFormattingDao formattingDocument,
        Reader reader, OutputStream outputStream) {
        FormattingValue value = new FormattingValue(formattingDocument.getDistributionType(),
            formattingDocument.getMimeType(), formattingDocument.getDocumentType(),
            formattingDocument.getMajorSchemaVersion(), formattingDocument.getMinorSchemaVersion(),
            formattingDocument.getLanguage(), formattingDocument.getCountry(), reader, outputStream,
            formattingDocument.getCourtId(), null);
        value.setXmlDocumentClobId(formattingDocument.getXmlDocumentClobId());
        value.setFormattingId(formattingDocument.getFormattingId());
        value.setFormattedDocumentBlobId(formattingDocument.getFormattedDocumentBlobId());
        return value;
    }

    protected void setXmlUtils(AbstractXmlMergeUtils xmlUtils) {
        this.xmlUtils = xmlUtils;
    }

    public AbstractXmlMergeUtils getXmlUtils() {
        return this.xmlUtils;
    }

    protected TranslationBundles getTranslationBundles() {
        if (translationBundles == null) {
            translationBundles = TranslationServices.getInstance().getTranslationBundles(null);
        }
        return translationBundles;
    }
}
