package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DistributionTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DocumentTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.MimeTypeType;

/**
 * Class XsltTransform.
 * 
 * @version $Revision$ $Date$
 */
public class XsltTransform extends AbstractTypeMarshal<String> implements java.io.Serializable {

    // --------------------------/
    // - Class/Member Variables -/
    // --------------------------/

    private static final long serialVersionUID = -5763673667108274173L;

    private static final Logger LOG = LoggerFactory.getLogger(XsltTransform.class);

    /**
     * Field _distributionType.
     */
    private DistributionTypeType distributionType;

    /**
     * Field _mimeType.
     */
    private MimeTypeType mimeType;

    /**
     * Field _documentType.
     */
    private DocumentTypeType documentType;

    /**
     * Field _majorVersion.
     */
    private int majorVersion;

    /**
     * keeps track of state for field: _majorVersion.
     */
    private boolean isMajorVersion;

    /**
     * Field _minorVersion.
     */
    private int minorVersion;

    /**
     * keeps track of state for field: _minorVersion.
     */
    private boolean isMinorVersion;

    /**
     * Field _xsltFileList.
     */
    private XsltFileList xsltFileList;

    // -----------/
    // - Methods -/
    // -----------/

    /**
     * Method deleteMajorVersion.
     * 
     */
    public void deleteMajorVersion() {
        this.isMajorVersion = false;
    }

    /**
     * Method deleteMinorVersion.
     * 
     */
    public void deleteMinorVersion() {
        this.isMinorVersion = false;
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }

    /**
     * Note: hashCode() has not been overriden.
     * 
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XsltTransform)) {
            return false;
        }

        XsltTransform temp = (XsltTransform) obj;
        return XsltTransformUtils.equalsDistributionType(temp.distributionType,
            this.distributionType)
            && XsltTransformUtils.equalsMimeType(temp.mimeType, this.mimeType)
            && XsltTransformUtils.equalsDocumentType(temp.documentType, this.documentType)
            && XsltTransformUtils.equalsVersion(temp.majorVersion, this.majorVersion,
                temp.minorVersion, this.minorVersion, temp.isMajorVersion, this.isMajorVersion,
                temp.isMinorVersion, this.isMinorVersion)
            && XsltTransformUtils.equalsFileList(temp.xsltFileList, this.xsltFileList);
    }

    /**
     * Returns the value of field 'distributionType'.
     * 
     * @return DistributionTypeType the value of field 'distributionType'.
     */
    public DistributionTypeType getDistributionType() {
        return this.distributionType;
    }

    /**
     * Returns the value of field 'documentType'.
     * 
     * @return DocumentTypeType the value of field 'documentType'.
     */
    public DocumentTypeType getDocumentType() {
        return this.documentType;
    }

    /**
     * Returns the value of field 'majorVersion'.
     * 
     * @return int the value of field 'majorVersion'.
     */
    public int getMajorVersion() {
        return this.majorVersion;
    }

    /**
     * Returns the value of field 'mimeType'.
     * 
     * @return MimeTypeType the value of field 'mimeType'.
     */
    public MimeTypeType getMimeType() {
        return this.mimeType;
    }

    /**
     * Returns the value of field 'minorVersion'.
     * 
     * @return int the value of field 'minorVersion'.
     */
    public int getMinorVersion() {
        return this.minorVersion;
    }

    /**
     * Returns the value of field 'xsltFileList'.
     * 
     * @return XsltFileList the value of field 'xsltFileList'.
     */
    public XsltFileList getXsltFileList() {
        return this.xsltFileList;
    }

    /**
     * Method hasMajorVersion.
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasMajorVersion() {
        return this.isMajorVersion;
    }

    /**
     * Method hasMinorVersion.
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasMinorVersion() {
        return this.isMinorVersion;
    }

    /**
     * Sets the value of field 'distributionType'.
     * 
     * @param distributionType the value of field 'distributionType'
     */
    public void setDistributionType(DistributionTypeType distributionType) {
        this.distributionType = distributionType;
    }

    /**
     * Sets the value of field 'documentType'.
     * 
     * @param documentType the value of field 'documentType'.
     */
    public void setDocumentType(DocumentTypeType documentType) {
        this.documentType = documentType;
    }

    /**
     * Sets the value of field 'majorVersion'.
     * 
     * @param majorVersion the value of field 'majorVersion'.
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
        this.isMajorVersion = true;
    }

    /**
     * Sets the value of field 'mimeType'.
     * 
     * @param mimeType the value of field 'mimeType'.
     */
    public void setMimeType(MimeTypeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the value of field 'minorVersion'.
     * 
     * @param minorVersion the value of field 'minorVersion'.
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
        this.isMinorVersion = true;
    }

    /**
     * Sets the value of field 'xsltFileList'.
     * 
     * @param xsltFileList the value of field 'xsltFileList'.
     */
    public void setXsltFileList(XsltFileList xsltFileList) {
        this.xsltFileList = xsltFileList;
    }

    /**
     * Method unmarshal.
     * 
     * 
     * 
     * @param reader Reader
     * @return XsltTransform
     */
    public static XsltTransform unmarshal(java.io.Reader reader)
        throws MarshalException, ValidationException {
        return (XsltTransform) Unmarshaller.unmarshal(XsltTransform.class, reader);
    }
}
