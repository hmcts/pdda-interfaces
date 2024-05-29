package uk.gov.hmcts.pdda.business.vos.formatting;

import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;

import java.io.OutputStream;
import java.io.Reader;
import java.util.Locale;

/**
 * <p>
 * Title:Class contains parameters required for making formatting decisions. When formatting a data
 * using XSLs.
 * </p>
 * <p>
 * Description: This is passed into the FormattingServices.process method
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bal Bhamra
 * @version 1.0
 */
@SuppressWarnings("PMD.ExcessiveParameterList")
public class FormattingValue {

    private final String distributionType;

    private final String mimeType;

    private final String documentType;

    private final Integer majorVersion;

    private final Integer minorVersion;

    private final Locale locale;

    private Reader reader;

    private final Integer courtId;

    private Integer formattingId;

    private Long xmlDocumentClobId;
    
    private Long formattedDocumentBlobId;

    private XhbCppListDao cppList;

    // Not marking as final as we need to alter the output stream for IWP
    private OutputStream outputStream;

    private static final String DOCUMENT_TYPE = "documentType";
    private static final String DISTRIBUTION_TYPE = "distributionType";
    private static final String MIME_TYPE = "mimeType";
    private static final String OUTPUT_STREAM = "outputStream";

    /**
     * Constructor taking in Formatting parameters.
     * 
     * @param distributionTypeIn String
     * @param mimeTypeIn String
     * @param documentTypeIn String
     * @param minorVersion optional
     * @param majorVersion optional
     * @param language optional
     * @param country optional
     * @param readerIn Reader
     * @param outputStreamIn OutputStream
     * @param courtId Integer
     */
    public FormattingValue(String distributionTypeIn, String mimeTypeIn, String documentTypeIn,
        Integer majorVersion, Integer minorVersion, String language, String country,
        Reader readerIn, OutputStream outputStreamIn, Integer courtId, XhbCppListDao cppList) {
        // Check arguments
        validateMandatory(distributionTypeIn, DISTRIBUTION_TYPE);
        validateMandatory(mimeTypeIn, MIME_TYPE);
        validateMandatory(documentTypeIn, DOCUMENT_TYPE);
        validateMandatory(outputStreamIn, OUTPUT_STREAM);

        // set attributes
        this.distributionType = distributionTypeIn;
        this.mimeType = mimeTypeIn;
        this.documentType = documentTypeIn;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.locale = createLocale(language, country);
        this.reader = readerIn;
        this.outputStream = outputStreamIn;
        this.courtId = courtId;
        setCppList(cppList);
    }

    /**
     * Getters returning individual formatting parameters.
     * 
     * @return Formatting Parameter
     */
    public String getDistributionType() {
        return distributionType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public Locale getLocale() {
        return locale;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setOutputStream(OutputStream outputStreamIn) {
        validateMandatory(outputStreamIn, OUTPUT_STREAM);
        this.outputStream = outputStreamIn;
    }

    @Override
    public String toString() {
        return "FormattingValue[distributionType=" + distributionType + ",mimeType=" + mimeType
            + ",documentType=" + documentType + ",majorVersion=" + majorVersion + ",minorVersion="
            + minorVersion + ",locale=" + locale + ",reader=" + reader + ",outputStream="
            + outputStream + ",courtId=" + courtId + "]";
    }

    private static Locale createLocale(String language, String country) {
        if (language == null) {
            return Locale.getDefault();
        }
        if (country == null) {
            return new Locale(language, "");
        }
        return new Locale(language, country);
    }

    public Integer getFormattingId() {
        return formattingId;
    }

    public void setFormattingId(Integer formattingId) {
        this.formattingId = formattingId;
    }

    public Long getXmlDocumentClobId() {
        return xmlDocumentClobId;
    }

    public void setXmlDocumentClobId(Long xmlDocumentClobId) {
        this.xmlDocumentClobId = xmlDocumentClobId;
    }

    public XhbCppListDao getCppList() {
        return cppList;
    }

    private void setCppList(XhbCppListDao cppList) {
        this.cppList = cppList;
    }

    private void validateMandatory(Object field, String name) {
        if (field == null) {
            throw new IllegalArgumentException(name + " cannot be null!");
        }
    }

    public void setFormattedDocumentBlobId(Long formattedDocumentBlobId) {
        this.formattedDocumentBlobId = formattedDocumentBlobId;
    }
    
    public Long getFormattedDocumentBlobId() {
        return formattedDocumentBlobId;
    }
}
