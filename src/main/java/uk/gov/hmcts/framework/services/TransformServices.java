package uk.gov.hmcts.framework.services;

import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.exception.Message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * <p>
 * Title: XSL Services.
 * </p>
 * <p>
 * Description: Insulates the application components from knowedge of XSL.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author William Fardell (Xdevelopment 2003)
 * @version 1.0
 */
public class TransformServices extends TemplateServices {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransformServices.class);

    /**
     * Transform the xml using the xsl, if specified use the locale and or parameter map.
     * 
     * @param document the xml transform
     * @param xslName the name of a resource containing the xsl style sheet
     * @param locale optional, the locale to resolve the xsl
     * @param parameterMap optional map of transformer parameterMap
     * @return the transformed xml
     */
    public String transform(Document document, String xslName, Locale locale, Map<?, ?> parameterMap) {
        if (document == null) {
            throw new IllegalArgumentException("document: null");
        }
        if (xslName == null) {
            throw new IllegalArgumentException("xslName: null");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Source: {}", documentToString(document));
        }
        return transform(new DOMSource(document), getSystemId(xslName, locale), locale, parameterMap);
    }

    /**
     * Transform the xml using the xsl for the specified locale.
     * 
     * @param source the xml transform
     * @param systemId the system Id of a resource containing the xsl style sheet
     * @param locale optional, the locale to resolve the xsl
     * @param parameterMap optional map of transformer parameterMap
     * @return the transformed xml
     */
    protected String transform(Source source, String systemId, Locale locale, Map<?, ?> parameterMap) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Transform: systemId: " + systemId + " locale: " + locale + " parameters: "
                + parameterMapToString(parameterMap));
            String xml = transformNow(source, systemId, locale, parameterMap);
            LOG.debug("Result: {}", xml);
            return xml;
        }
        return transformNow(source, systemId, locale, parameterMap);
    }

    private String transformNow(Source source, String systemId, Locale locale, Map<?, ?> parameterMap) {
        URIResolver resolver = getResolver(locale);
        Transformer transformer = getTransformer(systemId, resolver, parameterMap);

        Writer writerResult = new StringWriter();
        try {
            LOG.debug("transformNow({},{},{},{})", source, systemId, locale, parameterMap);
            transformer.transform(source, new StreamResult(writerResult));
        } catch (TransformerException te) {
            Message userMessage =
                new Message("xslservices.transformationerror", new Object[] {source, systemId, resolver});
            String logMessage = "Could not transform XML \"" + source + "\" using transform \"" + systemId + RESOLVED_BY
                + resolver + "\"";
            throw new CsUnrecoverableException(userMessage, te, logMessage);
        }
        return writerResult.toString();
    }

    //
    // Pipeline Methods
    //

    /**
     * Create a transformer for rendering pipelines.
     * 
     * @param locale the locale to use, if null use default
     * @param parameterMap any parameters that need to be added to the transformer
     * @return the new transformer
     */
    public Transformer getTransformer(Locale locale, Map<?, ?> parameterMap) {
        try {
            Transformer transformer = getTransformerFactory(getResolver(locale)).newTransformer();
            if (parameterMap != null) {
                Iterator<?> keys = parameterMap.keySet().iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object value = parameterMap.get(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Changeing parameter {} from {} to {} for transformer {}.", key,
                            transformer.getParameter(key), value, transformer);
                    }
                    transformer.setParameter(key, value);
                }
            }
            return transformer;
        } catch (TransformerException te) {
            throw new CsUnrecoverableException(new Message("xslservices.pipelineerror"), te,
                "Could not create a pipeline transformer.");
        }
    }

    /**
     * Gets the transformer for the xsl name and resolver, private because xsl classes should not be
     * used outside this class.
     * 
     * @param systemId the name of the xsl
     * @param resolver the resolver used to create the transform
     * @return the transformer
     */
    private Transformer getTransformer(String systemId, URIResolver resolver, Map<?, ?> paramaterMap) {
        try {
            LOG.debug("getTransformer({},{},{})", systemId, resolver, paramaterMap);
            return getTemplates(systemId, resolver, paramaterMap).newTransformer();
        } catch (TransformerException te) {
            Message userMessage = new Message("xslservices.transformererror", new Object[] {systemId, resolver});
            String logMessage = "Could not create transformer from template created from xsl \"" + systemId
                + RESOLVED_BY + resolver + "\"";

            throw new CsUnrecoverableException(userMessage, te, logMessage);
        }
    }

    //
    // Utilities
    //

    /**
     * Format parameter map for debug.
     * 
     * @param parameterMap Map
     * @return a string containing formatted parameter map
     */
    protected static final String parameterMapToString(Map<?, ?> parameterMap) {
        if (parameterMap == null) {
            return "null";
        } else {
            Iterator<?> parameterNames = parameterMap.keySet().iterator();
            if (parameterNames.hasNext()) {
                StringBuilder buffer = new StringBuilder();
                String parameterName = (String) parameterNames.next();
                buffer.append(parameterName).append(": ").append(parameterMap.get(parameterName));
                while (parameterNames.hasNext()) {
                    buffer.append(", ");
                    parameterName = (String) parameterNames.next();
                    buffer.append(parameterName).append(": ").append(parameterMap.get(parameterName));
                }
                return buffer.toString();
            } else {
                return "none";
            }
        }
    }

    /**
     * Convert a document to a String (this is used for debug output).
     * 
     * @param document the docuemnt to convert
     * @return a String containing the xml
     */
    private static String documentToString(Document document) {
        try {
            StringWriter buffer = new StringWriter();

            Serializer serializer =
                SerializerFactory.getSerializer(OutputPropertiesFactory.getDefaultMethodProperties("XML"));
            serializer.setWriter(buffer);
            DOMSerializer domSerializer = serializer.asDOMSerializer();
            domSerializer.serialize(document);

            return buffer.toString();
        } catch (IOException ioe) {
            return "Could not serialize Document: " + ioe;
        }
    }
}
