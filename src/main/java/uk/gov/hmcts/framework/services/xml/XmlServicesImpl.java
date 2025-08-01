package uk.gov.hmcts.framework.services.xml;

import org.eclipse.tags.shaded.org.apache.xpath.XPathAPI;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.business.vos.CsValueObject;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.pdda.business.services.formatting.AbstractXmlUtils;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**

 * Title: XMLServices.


 * Description: This class handles loading and creation of xml documents.


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Faisal Shoukat
 * @version $Id: XMLServicesImpl.java,v 1.42 2009/06/17 10:05:12 hewittm Exp $

 *          Change History -

 *          01/11/02 - JB - generateXMLFromPropSet now handles embedded property sets.

 *          22/11/02 - KB - added public Document createDocFromValue( CSValueObject obj ) throws
 *          CSXMLServicesException

 *          17/02/03 - AWD - Fixed bug Test Obs Id 49, Test Case 51994

 *          17/02/03 - AWD - generateXMLFromPropSet modified to build up an XML DOM document rather
 *          than a StringBuilder. Advantage is that substitution of special markup characters into
 *          entities is handled.

 *          03/03/03 - AWD - Method added to provide support for XML transformations
 *          05/03/03 - JB - Added addCollectionToXMLDoc to generate XML from collections
 *          03/04/03 - JB - The transformer now ouputs the XML header
 *          03/04/03 - JB - Backed out last change
 *          08/04/03 - JB - Another attempt to get the transformXML to output the header without
 *          affecting getStringXML
 *          08/05/03 - JB - Various changes to deal with special characters in the XML. Applying
 *          ISO-8859-1 encoding as this handles special chars better.
 *          19/11/03 - RL - Changing encoding to UTF-8 as this handles special chars even
 *          better!<br>
 *          Removing XMLTransform as XSLServices should be used

 */

public class XmlServicesImpl extends AbstractXmlUtils implements XmlServices {

    private static final Logger LOG = LoggerFactory.getLogger(XmlServicesImpl.class);

    private static final DocumentBuilderFactory DOCUMENTBUILDERFACTORY = DocumentBuilderFactory.newInstance();

    private static final String FACTORY_CONFIG_ERROR = "FactoryConfigurationError ";

    private static XmlServicesImpl instance = new XmlServicesImpl();

    protected XmlServicesImpl() {
        super();
    }

    /**
     * Get singleton instance of XMLServicesImpl.

     * @return XmlServicesImpl
     */
    public static XmlServicesImpl getInstance() {
        return instance;
    }

    /**
     * Convert a object implementing the CSValueObject interface to an xml document.

     * @param obj To work effectively the CSValueObject requires a default public constructor and public
     *        get methods for all vos that are required in the resulting xml document
     * @return Document xml representation of the CSValueObject
     * @throws CsXmlServicesException Exception
     */
    @Override
    public Document createDocFromValue(CsValueObject obj) {
        LOG.debug("START: createDocFromValue(CSValueObject) ATTRIBUTE = {}", obj);
        try {
            DocumentBuilder builder = DOCUMENTBUILDERFACTORY.newDocumentBuilder();
            Document doc = builder.newDocument();
            Marshaller marshaller = new Marshaller(doc);
            marshaller.setSuppressXSIType(false);
            marshaller.marshal(obj);
            return doc;
        } catch (ParserConfigurationException | MarshalException | ValidationException exception) {
            LOG.debug("caught Exception {}", exception.getMessage());
            CsXmlServicesException ex = new CsXmlServicesException(exception);
            CsServices.getDefaultErrorHandler().handleError(ex, XmlServicesImpl.class);
            throw ex;
        }


    }


    /**
     * Add a collection to the XML document being created.

     * @param doc Document
     * @param xmlToConvert Collection
     * @param tag String
     * @return Collection of DOM Elements
     */
    private Collection<Element> addCollectionToXmlDoc(Document doc, Collection<?> xmlToConvert, String tag) {
        String methodName = "addCollectionToXMLDoc() - ";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName + "called :: collection: " + xmlToConvert + " tag: " + tag);
        }
        List<Element> array = new ArrayList<>();

        Iterator<?> it = xmlToConvert.iterator();
        while (it.hasNext()) {
            Map<?, ?> currentMap = (Map<?, ?>) it.next();
            Element collectionElement = addPropertyMapToXmlDoc(doc, currentMap, tag);
            array.add(collectionElement);
        }

        LOG.debug("{} Exited OK :: {}", methodName, array);
        return array;
    }

    /**
     * Recursive method to handle embedded property sets.

     * @param doc document that the property map will be added to
     * @param xmlToConvert the properties object which contains the key value pairs
     * @param tag The root element of the xml document
     */
    private Element addPropertyMapToXmlDoc(Document doc, Map<?, ?> xmlToConvert, String tag) {
        String methodName = "addPropertyMapToXMLDoc() - ";

        LOG.debug("{} called :: tag: {}", methodName, tag);
        LOG.debug("{} No. properties to add : {}", methodName, xmlToConvert.size());
        Element element;

        // Create Document
        Element node = doc.createElement(tag);

        Set<?> keySet = xmlToConvert.keySet();
        LOG.debug("{} keyset: {}", methodName, keySet);

        Iterator<?> itr = keySet.iterator();
        LOG.debug("{} iterator: {}", methodName, itr);

        while (itr.hasNext()) {
            // Get Object and its key from the map
            String key = (String) itr.next();
            LOG.debug("{} Key: {}", methodName, key);

            Object obj = xmlToConvert.get(key);

            if (obj instanceof Map) {
                LOG.debug("{} Adding Map: {}", methodName, key);
                element = addPropertyMapToXmlDoc(doc, (Map<?, ?>) obj, key);
                node.appendChild(element);
            } else if (obj instanceof Collection) {
                LOG.debug("{} Adding Collection: {}", methodName, obj.getClass().getName());
                Collection<?> coll = this.addCollectionToXmlDoc(doc, (Collection<?>) obj, key);
                Iterator<?> it = coll.iterator();
                while (it.hasNext()) {
                    node.appendChild((Element) it.next());
                }
            } else {
                LOG.debug("{} Adding element: {} value: {}", methodName, key, obj);
                element = doc.createElement(key);
                if (obj != null) {
                    String value = obj.toString();
                    LOG.debug("value does not equal null has no length");
                    element.appendChild(doc.createTextNode(String.valueOf(value)));
                    LOG.debug("{} Tag added, name: {}, value: {}", methodName, key, value);
                }
                node.appendChild(element);
            }
        }

        return node;
    }

    /**
     * createDocFromString.

     * @param xmlContent String
     * @return Document
     * @throws CsXmlServicesException Exception
     */
    @Override
    public Document createDocFromString(String xmlContent) {
        Document xml;

        try {
            LOG.debug("createDocFromString({})", xmlContent);
            xml = DocumentUtils.createInputDocument(xmlContent);
        } catch (ParserConfigurationException | FactoryConfigurationError | IOException | SAXException exception) {
            LOG.debug(FACTORY_CONFIG_ERROR + exception);
            CsXmlServicesException xmlE = new CsXmlServicesException(exception);
            CsServices.getDefaultErrorHandler().handleError(xmlE, XmlServicesImpl.class);
            throw xmlE;
        }

        return xml;

    }

    /**
     * Retrieves the value of an xpath from an xml string.

     * @param xmlString The xml to search
     * @param xpath The xpath to search for
     * @return The value of the xpath
     * @throws CsUnrecoverableException If the xpath is not found in the xml
     */
    @Override
    public String getXpathValueFromXmlString(String xmlString, String xpath) {
        LOG.debug("getXpathValueFromXpathString() start with " + "xmlString = " + xmlString + " and xpath = " + xpath);
        // check parameters we're going to use
        if (xmlString == null || xpath == null) {
            throw new IllegalArgumentException("Both xmlString and xpath parameters must both be provided "
                + "with not null values to the getXpathValueFromXpathString() method");
        }

        try {
            LOG.debug("getXpathValueFromXmlString({},{})", xmlString, xpath);
            // First turn the string XML into a DOM
            Document eventDocument = DocumentUtils.createInputDocument(xmlString);

            // Then using the XPathAPI to get the value of the node
            Node valueNode = XPathAPI.selectSingleNode(eventDocument, xpath);

            if (valueNode != null) { // Is there a node for the XPath?
                LOG.debug("getXpathFromLogEntry() : Returning xpath value {}", valueNode.getNodeValue());
                return valueNode.getNodeValue();
            } else {
                throw new CsUnrecoverableException(
                    "Could not get a node for the XPath: " + xpath + " for the xml: " + xmlString);
            }
        } catch (ParserConfigurationException | FactoryConfigurationError | IOException | SAXException
            | TransformerException exception) {
            LOG.error("Error reading input XML: {}", xmlString, exception);
            CsServices.getDefaultErrorHandler().handleError(exception, getClass());
            throw new CsUnrecoverableException("Error reading input XML: " + xmlString, exception);
        }
    }

    /**
     * Adds the specified element before the beforeTag in the passed document.

     * @param document Document to which the element is added
     * @param tagName Name of the element that is added
     * @param value Node value of the element
     * @param beforeTag Tag before whish the new element is added
     */
    @Override
    public void addElementByTagName(Document document, String tagName, String value, String beforeTag) {
        if (document == null) {
            LOG.warn(getClass().getName() + "addElementByTagName() :: No XML document loaded");
            return;
        }

        NodeList beforeNodeList = document.getElementsByTagName(beforeTag);

        if (beforeNodeList.getLength() > 0) {
            Element beforeElement = (Element) beforeNodeList.item(0);
            Element newElement = document.createElement(tagName);
            newElement.appendChild(document.createTextNode(value));

            beforeElement.insertBefore(newElement, beforeElement.getFirstChild());
        }
    }

}
