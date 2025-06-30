package uk.gov.hmcts.pdda.business.services.validation.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import uk.gov.hmcts.pdda.business.services.validation.ValidationException;
import uk.gov.hmcts.pdda.business.services.validation.ValidationResult;
import uk.gov.hmcts.pdda.business.services.validation.ValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;

/**
 * Simple Service for Validating XML.
 * 
 * @author William Fardell
 */
@SuppressWarnings({"squid:S2755", "PMD.LawOfDemeter","PMD.CloseResource"})
public class SaxValidationService implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(SaxValidationService.class);
    private static final String DISALLOW_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    private final EntityResolver entityResolver;

    private SchemaFactory schemaFactory;
    private SAXParserFactory saxParserFactory;

    public SaxValidationService(final EntityResolver entitiyResolver) {
        this.entityResolver = entitiyResolver;
    }

    // Junit constructor
    public SaxValidationService(final EntityResolver entityResolver, SchemaFactory schemaFactory,
        SAXParserFactory saxParserFactory) {
        this(entityResolver);
        this.schemaFactory = schemaFactory;
        this.saxParserFactory = saxParserFactory;
    }

    @Override
    public ValidationResult validate(final String xml, final String schemaName) throws ValidationException {
        LOG.debug("entered validate method");

        try {
            SAXParserFactory factory = getSaxParserFactory();
            factory.setNamespaceAware(true);
            
            URL url = Thread.currentThread().getContextClassLoader()
                .getResource("config/xsd/CourtService_CPP-v1-0.xsd");
            LOG.debug("Resolved CourtService_CPP-v1-0.xsd to URL: {}", url);
            
            try {
                LOG.debug("Creating Schema for: {}", schemaName);
                factory.setSchema(getSchemaFactory().newSchema(getSaxSourceFromClasspath(schemaName)));
            } catch (SAXException e) {
                LOG.error("Schema compilation failed for {}: {}", schemaName, e.getMessage(), e);
                throw new ValidationException("Schema compilation failed for: " + schemaName, e);
            }

            SAXParser parser = factory.newSAXParser();

            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(entityResolver);
            
            ErrorHandlerValidationResult result = new ErrorHandlerValidationResult();
            reader.setErrorHandler(result);
            reader.parse(new InputSource(new StringReader(xml)));
            LOG.debug("Valid: {}", result.isValid());
            if (!result.isValid()) {
                LOG.debug("Validation Failed: {}", result);
            }
            return result;

        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new ValidationException("An error occurred validating.", e);
        }
    }
    
    public SAXSource getSaxSourceFromClasspath(String fullPath) throws SAXException {
        LOG.debug("entered getSaxSourceFromClasspath method");
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath);

        if (is == null) {
            throw new SAXException("Unable to find XSD at " + fullPath);
        }
        
        String systemId = this.getClass().getResource("/" + fullPath).toString();
        LOG.debug("Creating InputSource for schema: {}, systemId: {}", fullPath, systemId);
        
        URL url = Thread.currentThread().getContextClassLoader()
            .getResource("config/xsd/CourtService_CPP-v1-0.xsd");
        LOG.debug("Resolved CourtService_CPP-v1-0.xsd to URL: {}", url);

        InputSource inputSource = new InputSource(is);
        // This systemId must be a pseudo-URI (classpath URL or similar) to enable relative includes to work.
        inputSource.setSystemId(this.getClass().getResource("/" + fullPath).toString());

        return new SAXSource(inputSource);
    }


    protected SchemaFactory getSchemaFactory() throws SAXNotRecognizedException, SAXNotSupportedException {
        if (schemaFactory == null) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // Disallow DOCTYPE declarations to prevent XXE attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            // Allow schema includes from classpath (and optionally local file system)
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "jar:*,classpath,file");

            // Block all DTDs
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

            // Cache for reuse if desired
            schemaFactory = factory;
        }
        return schemaFactory;
    }


    protected SAXParserFactory getSaxParserFactory()
        throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (saxParserFactory == null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature(DISALLOW_DECL, true);
            return factory;
        }
        return saxParserFactory;
    }
}