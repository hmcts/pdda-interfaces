package uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda;

import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DistributionTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DocumentTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.MimeTypeType;

/**
 * Class XsltTransformUtils.

 */
public final class XsltTransformUtils {

    private XsltTransformUtils() {

    }

    public static boolean equalsMimeType(MimeTypeType mimeType1, MimeTypeType mimeType2) {
        if (mimeType2 != null) {
            if (!mimeType2.equals(mimeType1)) {
                return false;
            }
        } else if (mimeType1 != null) {
            return false;
        }
        return true;
    }

    public static boolean equalsDistributionType(DistributionTypeType distributionType1,
        DistributionTypeType distributionType2) {
        if (distributionType2 != null) {
            if (!distributionType2.equals(distributionType1)) {
                return false;
            }
        } else if (distributionType1 != null) {
            return false;
        }
        return true;
    }

    public static boolean equalsDocumentType(DocumentTypeType documentType1,
        DocumentTypeType documentType2) {
        if (documentType2 != null) {
            if (!documentType2.equals(documentType1)) {
                return false;
            }
        } else if (documentType1 != null) {
            return false;
        }
        return true;
    }

    public static boolean equalsFileList(XsltFileList xsltFileList1, XsltFileList xsltFileList2) {
        if (xsltFileList2 != null) {
            if (!xsltFileList2.equals(xsltFileList1)) {
                return false;
            }
        } else if (xsltFileList1 != null) {
            return false;
        }
        return true;
    }

    public static boolean equalsVersion(int majorVersion1, int majorVersion2, int minorVersion1,
        int minorVersion2, boolean isMajorVersion1, boolean isMajorVersion2,
        boolean isMinorVersion1, boolean isMinorVersion2) {
        return majorVersion2 == majorVersion1 && isMajorVersion2 == isMajorVersion1
            && minorVersion2 == minorVersion1 && isMinorVersion2 == isMinorVersion1;
    }


}
