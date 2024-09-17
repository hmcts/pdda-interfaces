package uk.gov.hmcts.pdda.business.services.publicnotice;

import jakarta.ejb.EJBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.exception.CsConfigurationException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.vos.services.publicnotice.DefinitivePublicNoticeStatusValue;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * <p>
 * Title: Reads in the XML file and creates a Map which is used in PublicNoticeManipulator.
 * </p>
 * <p>
 * Description: see title
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Pat Fox, Bob Boles
 * @created 24 February 2003
 */
public final class PublicNoticeXmlHelper {

    // Keys into Properties file for the XmlHelper
    private static final String XML_BASE_PATH = "XML_BASE_PATH";
    private static final String XML_CONFIG_FILE = "XML_CONFIG_FILE";
    // Properties file for general public notice configuration
    private static final String PROPERTIESFILENAME = "publicnotice";
    private static final String COURT_LOG_EVENT = "CourtLogEvent";
    private static final String DEFINITIVE_PUBLIC_NOTICE = "DefinitivePublicNotice";

    private final Map<Integer, List<DefinitivePublicNoticeStatusValue>> manipulatorMap 
        = new ConcurrentHashMap<>();

    private static Logger log = LoggerFactory.getLogger(PublicNoticeXmlHelper.class);

    private static PublicNoticeXmlHelper instance = new PublicNoticeXmlHelper();

    private Properties configProperties;

    /**
     * Gets the instance attribute of the PublicNoticeXmlHelper class.
     * 
     * @return The instance value
     */
    public static PublicNoticeXmlHelper getInstance() {
        return instance;
    }

    /**
     * Constructor for the PublicNoticeXmlHelper object.
     */
    private PublicNoticeXmlHelper() {
        // load properties file
        loadPublicNoticeProperties();

        String xmlFileName = configProperties.getProperty(XML_CONFIG_FILE);

        if (log.isDebugEnabled()) {
            log.debug("Loading xml file : {}", xmlFileName);
        }
        // load the xmlfile to the Map
        try {
            loadXmlToMap(xmlFileName);
        } catch (ParserConfigurationException ex) {
            CsServices.getDefaultErrorHandler().handleError(ex, this.getClass());
            throw new EJBException(ex);
        }

    }

    /**
     * Gets the manipulatorMap attribute of the PublicNoticeXmlHelper object.
     * 
     * @return The manipulatorMap value
     */
    public Map<Integer, List<DefinitivePublicNoticeStatusValue>> getManipulatorMap() {
        return manipulatorMap;
    }

    /**
     * Description of the Method.
     * 
     * @param xmlSourceFile Description of the Parameter
     */
    private void loadXmlToMap(String xmlSourceFile) throws ParserConfigurationException {
        // Get a DocumentBuilderFactory

        Document doc;
        DocumentBuilderFactory dbf = DocumentUtils.getDocumentBuilderFactory();
        String xmlBasePath = configProperties.getProperty(XML_BASE_PATH);

        if (log.isDebugEnabled()) {
            log.debug("Loading file from :{}{}", xmlBasePath, xmlSourceFile);
        }

        URL myUrl = this.getClass().getResource(xmlBasePath + xmlSourceFile);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource theStream = new InputSource(myUrl.openStream());

            // Parse our xml file
            doc = db.parse(theStream);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            CsServices.getDefaultErrorHandler().handleError(ex, this.getClass());
            throw new EJBException(ex);
        }

        // Get the root node.
        Element elem = doc.getDocumentElement();

        // Traverse the tree.
        traverse(elem);

    }

    /**
     * Traverses the XML tree, fore each courtlogEvet tag it builds an arrayList of objects that are
     * keyed on the EventId each element in the array list contains an {definitivePNId &
     * isActive/status}.
     * 
     * @param node Description of the Parameter
     */
    private void traverse(Node node) {
        log.debug(" Entering the traverse ");

        // if it's an element node
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            // if it's a <courtlogEvent> tag then we create an
            // array list and add to Map
            if (COURT_LOG_EVENT.equals(node.getNodeName())) {
                NamedNodeMap theAttributes = node.getAttributes();

                String theEventType = theAttributes.item(0).getNodeValue();

                if (log.isDebugEnabled()) {
                    log.debug("The event type is {}", theEventType);
                }

                List<DefinitivePublicNoticeStatusValue> theList = createListOfDefPn(node);

                log.debug(" Size of Map currently is {}", getManipulatorMap().size());
                log.debug(" Adding component with Key {}", Integer.valueOf(theEventType));

                getManipulatorMap().put(Integer.valueOf(theEventType), theList);

                log.debug(" Size of Map after addition is :{}", getManipulatorMap().size());

                // if it's not a <courtlogEvent> tag,
                // we want to check all of it's children
            } else {
                NodeList nl = node.getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    traverse(nl.item(i));
                }
            }
        } else {
            // if it's not an element, just return

            log.debug("Exiting traverse() ");
        }
    }

    // end traverse

    /**
     * Description of the Method.
     * 
     * @param node Description of the Parameter
     * @return Description of the Return Value
     */
    private List<DefinitivePublicNoticeStatusValue> createListOfDefPn(Node node) {
        List<DefinitivePublicNoticeStatusValue> theList = new ArrayList<>();

        if (log.isDebugEnabled()) {
            log.debug(" Entering createListOfDefPN() ");
        }

        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node defnotice = nl.item(i);
            log.debug(" Node name = {}", defnotice.getNodeName());
            if (DEFINITIVE_PUBLIC_NOTICE.equals(defnotice.getNodeName())) {

                log.debug(" Getting the attributes");
                NamedNodeMap theAttributes = defnotice.getAttributes();

                // get the length of attributes
                log.debug(" length of Attributes {}", theAttributes.getLength());
                // print out the attributes

                Node defPnid = theAttributes.item(0);
                log.debug(" Attribute name is {}", defPnid.getNodeName());
                log.debug(" Attribute value is {}", defPnid.getNodeValue());

                Node defPnStatus = theAttributes.item(1);
                log.debug(" Attribute name is {}", defPnStatus.getNodeName());
                log.debug(" Attribute value is {}", defPnStatus.getNodeValue());

                theList.add(new DefinitivePublicNoticeStatusValue(Integer.valueOf(defPnid.getNodeValue()),
                    Boolean.parseBoolean(defPnStatus.getNodeValue())));
            }
        }

        log.debug(" Exiting createListOfDefPN() ");
        return theList;
    }

    /**
     * Gets the publicNoticeProperties attribute of the PublicNoticeXmlHelper object.
     */
    private void loadPublicNoticeProperties() {

        if (log.isDebugEnabled()) {
            log.debug("Entering getPublicNoticeProperties() ");
        }

        try {
            configProperties = CsServices.getConfigServices().getProperties(PROPERTIESFILENAME);
        } catch (CsConfigurationException e) {
            CsServices.getDefaultErrorHandler().handleError(e, getClass(), e.toString());
            log.error("Cannot find properties file: " + PROPERTIESFILENAME, e);
            throw new EJBException("Xhibit_Messaging.Unable_To_Locate_Properties" + e.getMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Exiting getPublicNoticeProperties() ");
        }
    }

}
