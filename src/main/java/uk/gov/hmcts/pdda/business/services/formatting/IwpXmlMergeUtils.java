package uk.gov.hmcts.pdda.business.services.formatting;

import javax.xml.xpath.XPathExpressionException;

public class IwpXmlMergeUtils extends AbstractXmlMergeUtils {

    private static final String IWP = "IWP";

    public IwpXmlMergeUtils() throws XPathExpressionException {
        super("currentcourtstatus/court/courtsites/courtsite/courtrooms");
    }

    @Override
    public String[] getNodeMatchArray() {
        return new String[] {"courtsitename"};
    }

    @Override
    public String[] getNodePositionArray() {
        return InternetWebPageUtils.getNodePositionArray();
    }

    @Override
    public String getMergeType() {
        return IWP;
    }
}
