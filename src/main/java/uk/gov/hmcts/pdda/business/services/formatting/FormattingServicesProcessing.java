package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.ejb.EJBException;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.services.TranslationServices;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingDao;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CourtUtils;
import uk.gov.hmcts.pdda.business.services.pdda.BlobHelper;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.TranslationUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

/**
 * FormattingServicesProcessing.
 */

@SuppressWarnings("PMD.ExcessiveImports")
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

        } catch (TransformerException ex) {
            throw new FormattingException(MERGING_EXCEPTION_STRING
                + formattingValue.getFormattingId() + DELIMITER + ex.getMessage(), ex);
        }
    }

    // private StringBuilder getFormattedInternetWebpage(List<StringBuilder> pddaClobAsString,
    // FormattingValue formattingValue, String courtSite,
    // Document cppDocument, final String translationXml) throws IOException {
    // if (pddaClobAsString == null || pddaClobAsString.isEmpty()) {
    // Long pddaClobId = getLatestXhibitClobId(formattingValue.getCourtId(), IWP,
    // formattingValue.getLocale().getLanguage(), courtSite);
    // if (pddaClobId != null) {
    // return processIwpCppFormatting(pddaClobId, formattingValue, cppDocument,
    // translationXml);
    // // Commented out - not required for PDDA, but may be required in the future
    // // saveDocInfo(formattingValue, courtSite);
    // }
    // } else if (!CourtUtils.isCourtSiteInClob(pddaClobAsString, courtSite)) {
    // // we have to go and get the next xhibit document with this courtsite in it
    // Long pddaClobId = getLatestXhibitClobId(formattingValue.getCourtId(), IWP,
    // formattingValue.getLocale().getLanguage(), courtSite);
    // // now we need to get the latest xhibit document
    // if (pddaClobId == null) {
    // return getFormattedInternetWebpageCourtSite(formattingValue, cppDocument,
    // translationXml, pddaClobId);
    // }
    // }
    // return null;
    // }

    // private StringBuilder getFormattedInternetWebpageCourtSite(
    // FormattingValue formattingValue, Document cppDocument, final String translationXml,
    // Long pddaClobId) throws IOException {
    // // create a new xhb_formatting row first -- as this is a new merged
    // // xml that needs to be processed
    // Optional<XhbFormattingDao> createdBv = getXhbFormattingDao(formattingValue);
    // if (createdBv.isPresent()) {
    // try (OutputStream outputStream = FormattingServiceUtils.getByteArrayOutputStream()) {
    // FormattingValue cppFormattingVal =
    // getFormattingValue(createdBv.get(), null, outputStream);
    // return processIwpCppFormatting(pddaClobId, cppFormattingVal, cppDocument,
    // translationXml);
    // // Commented out - not required for PDDA, but may be required in
    // // the
    // // future
    // // saveDocInfo(cppFormattingVal, courtSite);
    // }
    // }
    // return null;
    // }

    private Document getCppDocument(Long xmlDocumentClobId)
        throws SAXException, IOException, ParserConfigurationException {
       // String cppClobAsString = getClobData(xmlDocumentClobId); //TODO
        String cppClobAsString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<?xml-stylesheet type=\"text/xsl\" href=\"InternetWebPageTemplate.xsl\"?>"
            + "<currentcourtstatus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
            "<court><courtname>SNARESBROOK</courtname><courtsites><courtsite><courtsitename>SNARESBROOK</courtsitename><courtrooms><courtroom><currentstatus/><courtroomname>Court 1</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 2</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 3</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 4</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 5</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 6</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 7</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 8</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 9</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 10</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 11</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 12</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 13</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 14</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 15</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 16</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 17</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 18</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 19</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 20</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 21</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 22</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 24</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 88</courtroomname></courtroom></courtrooms></courtsite><courtsite><courtsitename>PRESTON-B</courtsitename><courtrooms><courtroom><currentstatus/><courtroomname>Crown Court 1</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 2</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Crown Court 3</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Crown Court 5</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Crown Court 10</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Crown Court 11</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Crown Court 12</courtroomname></courtroom></courtrooms></courtsite><courtsite><courtsitename>VILNIUS</courtsitename><courtrooms><courtroom><currentstatus/><courtroomname>Court 9</courtroomname></courtroom><courtroom><currentstatus/><courtroomname>Court 10</courtroomname></courtroom></courtrooms></courtsite><courtsite><courtsitename>VILLAGE HALL</courtsitename><courtrooms><courtroom><currentstatus/><courtroomname>Court 5</courtroomname></courtroom></courtrooms></courtsite></courtsites></court><datetimestamp><dayofweek>Tuesday</dayofweek><date>05</date><month>January</month><year>2021</year><hour>14</hour><min>32</min></datetimestamp><pagename>snaresbrook</pagename></currentcourtstatus>";
        return DocumentUtils.createInputDocument(cppClobAsString);
    }

    protected StringBuilder processIwpCppFormatting(final Long clobId,
        final FormattingValue formattingValue, final Document cppDocument,
        final String translationXml) {
        if (clobId != null) {
            try {

                LOG.debug(" Found an XHIBIT document to process {}", clobId);
                StringBuilder buff = new StringBuilder();
                buff.append(getClobData(clobId));

                Document xhibitDocument = DocumentUtils.createInputDocument(buff.toString());

                Document mergedDocument = getXmlUtils().merge(xhibitDocument, cppDocument);
                TransformerFactory transF = TranslationUtils.getTransformerFactory();
                Transformer trans = transF.newTransformer();
                StringWriter write = new StringWriter();
                trans.transform(new DOMSource(mergedDocument), new StreamResult(write));

                formattingValue.setReader(new StringReader(write.getBuffer().toString()));

                Writer buffer = TransformerUtils.transform(getXslServices(), getFormattingConfig(),
                    formattingValue, translationXml, new StringWriter());
                LOG.debug("\n\n\n\n\n\n\nprocessDocument({}):\n {}", formattingValue, buffer);
                return buff;

            } catch (IOException | XPathExpressionException | ParserConfigurationException
                | TransformerException | SAXException ex) {
                throw new FormattingException(MERGING_EXCEPTION_STRING
                    + formattingValue.getFormattingId() + DELIMITER + ex.getMessage(), ex);
            }
        } else {
            LOG.error("No Xhibit documents to process");
            throw new FormattingException(" There are no Xhibit documents to process");
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
