package uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public final class CourtUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CourtUtils.class);
    private static final String COURTSITENAME = "courtsitename";
    private static final String YES = "Y";
    
    private CourtUtils() {
    }
    
    public static boolean isCppCourt(List<XhbCourtDao> courts) {
        if (courts != null && !courts.isEmpty()) {
            return YES.equals(courts.get(0).getCppCourt());
        }
        return false;
    }
    
    public static boolean hasCourtSites(String clob) {
        // <courtsitename> followed by anything but <
        String matchString = "<courtsitename>[^<]";
        Pattern patt = Pattern.compile(matchString);
        Matcher match = patt.matcher(clob);
        return match.find();
    }
    
    public static boolean isCourtSiteInClob(List<StringBuilder> xhibitClobAsString, String courtSite) {
        boolean found = false;
        for (StringBuilder clobAsString : xhibitClobAsString) {
            if (clobAsString.indexOf("<courtsitename>" + courtSite) >= 0) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    /**
     * Bring back all the court sites. This is used for merging CPP to XHIBIT documents.

     * @param document Document containing the court sites
     * @return a list of courtsites
     * @throws XPathExpressionException Exception
     */
    public static List<Node> getCourtSites(Document document) throws XPathExpressionException {
        LOG.debug("getCourtSites({})", document);
        List<Node> results = new ArrayList<>();
        String[] rootNodes = {"currentcourtstatus/court/courtsites/courtsite/courtsitename"};
        XPathExpression[] rootNodeExpression = new XPathExpression[rootNodes.length];
        for (int nodeNo = 0; nodeNo < rootNodes.length; nodeNo++) {
            rootNodeExpression[nodeNo] = XPathFactory.newInstance().newXPath().compile(rootNodes[nodeNo]);
        }

        for (XPathExpression rootNode : rootNodeExpression) {
            NodeList nodeList = (NodeList) rootNode.evaluate(document, XPathConstants.NODESET);
            for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                if (Node.ELEMENT_NODE == nodeList.item(nodeNo).getNodeType()
                    && COURTSITENAME.equals(nodeList.item(nodeNo).getNodeName())) {
                    Element element = (Element) nodeList.item(nodeNo);
                    Node el = element.getFirstChild();
                    if (el != null) {
                        results.add(el);
                    }

                }
            }
        }

        return results;
    }
}
