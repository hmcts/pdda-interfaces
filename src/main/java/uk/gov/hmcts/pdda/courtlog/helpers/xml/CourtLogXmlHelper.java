package uk.gov.hmcts.pdda.courtlog.helpers.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogCrudValue;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Title: CourtLogXmlHelper.

 * Copyright: Copyright (c) 2002

 * Company: EDS

 * @author Joseph Babad / Paul Fitton
 * @version $Revision: 1.9 $
 */
public final class CourtLogXmlHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CourtLogXmlHelper.class);

    private static final String SCHEMA_BASE = "/config/courtlog/schemas/";

    private static final String SCHEMA_FILE_EXTENSION = ".xsd";

    // XML
    private static final String ROOT_NODE_NAME = "event";

    private CourtLogXmlHelper() {
        super();
    }
    
    /**
     * Return an XML representation of the vos properties.

     * @param courtLogCrudValue object
     * @return xml string
     */
    public static String getXml(CourtLogCrudValue courtLogCrudValue) {
        String methodName = "getXML() - {}{}";
        LOG.debug(methodName, "entry - value : {}", courtLogCrudValue);

        courtLogCrudValue.setProperty(CourtLogCrudValue.EVENT_TYPE, courtLogCrudValue.getEventType().toString());
        courtLogCrudValue.setProperty(CourtLogCrudValue.ENTRY_FREE_TEXT, courtLogCrudValue.getEntryFreeText());

        CourtLogMarshaller marshaller = new CourtLogMarshaller();
        String xml = marshaller.marshall(courtLogCrudValue.getPropertyMap(), ROOT_NODE_NAME);

        // Add no name space schema location to the generated xml...
        String schemaXml = addSchema(xml, courtLogCrudValue.getEventType());

        LOG.debug(methodName, "Generated XML : {}", schemaXml);

        return schemaXml;
    }

    /**
     * Add schema to the xml.

     * @param xml generated xml
     * @return xml with schema added
     */
    private static String addSchema(String xml, Integer eventType) {
        LOG.debug("addSchema() - eventType = {}{}{}", eventType, "; xml = ", xml);

        final int start = xml.indexOf("<event>");
        String resultXml = xml;
        
        if (start != -1) {
            String schema = "<event xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
                    + "xsi:noNamespaceSchemaLocation='" + eventType + ".xsd'>";

            resultXml = xml.replaceFirst("<event>", schema);
        }
        return resultXml.trim();
    }

    /**
     * Ensure that the passed in xml is valid, determined by the schema of the
     * event type passed in.

     * @param xmlString the xml to validate
     * @param eventType the event type used to acquire the xsd file to validate against
     * @see #getSchema(Integer)
     */
    public static void validateXml(String xmlString, Integer eventType) {
        String schemaLocation = getSchema(eventType);
        CsServices.getXmlServices().validateXml(xmlString, schemaLocation);
    }

    public static Map getPropertySet(String xmlFragment) {
        LOG.debug("getPropertySet() - entry - xml : {}", xmlFragment);

        // Generate multi-level hash map from xml
        CourtLogMarshaller marshaller = new CourtLogMarshaller();
        Map propertyMap = marshaller.unmarshall(xmlFragment);

        // Diagnostic Logging
        LOG.debug("getPropertySet() - Newly generated HashMap size : {}", propertyMap.size());
        Iterator keys = propertyMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            LOG.debug("getPropertySet() - key : {}{}{}", key, ", value : ", propertyMap.get(key));
        }
        return propertyMap;
    }

    /**
     * Method to acquire the full schema location for the passed in event type.

     * @param eventType
     *            The event type that we want the full location for.
     * @return A <code>String</code> of the full location.
     */
    private static String getSchema(Integer eventType) {
        LOG.debug("getSchema() - entry - eventType : {}", eventType);
        return SCHEMA_BASE + eventType.toString() + SCHEMA_FILE_EXTENSION;
    }
    
    /**
     * Create a <code>Document</code> object from the passed in source.

     * @param src
     *            The <code>InputStream</code> that the xml document is to be
     *            read from
     * @return An implementation of <code>Document</code>
     */
    public static Document createDocument(InputStream src) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // security
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(src);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new CourtLogRuntimeException(e);
        }
    }

    /**
     * Create a <code>Document</code> object from the passed in xml.

     * @param xml
     *            The xml we want as a Document
     * @return An implementation of <code>Document</code>
     */
    public static Document createDocument(String xml) {
        try (InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(stream);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new CourtLogRuntimeException(e);
        }
    }
}
