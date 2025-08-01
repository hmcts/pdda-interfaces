package uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.EventType;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.exceptions.UnrecognizedEventException;
import uk.gov.hmcts.pdda.web.publicdisplay.workflow.pub.ruleengine.exceptions.RulesConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**

 * Title: Rule Configuration.


 * Description: This class Creates conditional documents containing a display document type, the
 * associated rules for a event type.


 * Copyright: Copyright (c) 2003


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: RulesConfiguration.java,v 1.4 2006/06/05 12:32:37 bzjrnl Exp $
 */

@SuppressWarnings("PMD.LawOfDemeter")
public class RulesConfiguration {

    /**
     * The file for the XML Configuration.
     */
    private static final String XML_RULE_FILE =
        RulesConfiguration.class.getPackage().getName().replace('.', '/') + "/RuleMapping.xml";

    /**
     * Map of event types to Conditional Documents.
     */
    private EventMappings eventMap;

    private RulesConfigurationHandler rulesConfigurationHandler;

    /**
     * Empty constructor.
     */
    protected RulesConfiguration() {
        super();
    }

    /**
     * JUnit constructor.
     */
    protected RulesConfiguration(RulesConfigurationHandler rulesConfigurationHandler) {
        this();
        this.rulesConfigurationHandler = rulesConfigurationHandler;
    }

    /**
     * Creates a populated rules configuration.

     * @return RulesConfiguration
     * @throws RulesConfigurationException Exception
     */
    public static RulesConfiguration newConfiguration() {
        RulesConfiguration rc = new RulesConfiguration();
        rc.loadConfiguration();
        return rc;
    }

    /**
     * Returns the conditional documents that can be effected by the passed in event. The
     * conditional documents may not be valid for the generated event and will need to be checked.

     * @param eventType EventType
     * @return ConditionalDocument array. May be empty but never null.
     */
    public ConditionalDocument[] getConditionalDocumentsForEvent(EventType eventType) {
        try {
            return eventMap.getConditionalDocumentsForEvent(eventType);
        } catch (Exception ex) {
            throw new UnrecognizedEventException(eventType, ex);
        }
    }

    /**
     * Reads the XML and populates the HashMaps.

     * @throws RulesConfigurationException Exception
     */
    public void loadConfiguration() {
        SAXParser saxParser = getParser();
        RulesConfigurationHandler handler = getRulesConfigurationHandler();
        try {
            saxParser.getXMLReader().setErrorHandler(handler);
        } catch (SAXException ex) {
            throw new RulesConfigurationException("Error registering Error handler", ex);
        }

        try {
            saxParser.parse(getXmlStream(), handler);
            eventMap = handler.getEventMappings();
        } catch (SAXException ex) {
            throw new RulesConfigurationException("Error parsing document", ex);
        } catch (IOException ex) {
            throw new RulesConfigurationException("Error reading IOStream for " + XML_RULE_FILE,
                ex);
        }
    }

    /**
     * Get the Rule configuration XML file as an Input Stream.

     * @return InputStream
     */
    private InputStream getXmlStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_RULE_FILE);
    }

    /**
     * Get the SAXParserFactory.

     * @return SAXParserFactory
     */
    private SAXParserFactory getSaxParserFactory()
        throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Protect against XXE attacks
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory;
    }

    /**
     * Get an instance of the SAXParser.

     * @return SAXParser
     * @throws RulesConfigurationException Exception
     */
    private SAXParser getParser() {
        SAXParser saxParser;
        try {
            saxParser = getSaxParserFactory().newSAXParser();
        } catch (FactoryConfigurationError | SAXException | ParserConfigurationException ex) {
            throw new RulesConfigurationException("Error getting parser", ex);
        }
        return saxParser;
    }

    private RulesConfigurationHandler getRulesConfigurationHandler() {
        if (rulesConfigurationHandler == null) {
            return new RulesConfigurationHandler();
        }
        return rulesConfigurationHandler;
    }
}
