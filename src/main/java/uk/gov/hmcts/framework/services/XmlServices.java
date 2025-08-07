package uk.gov.hmcts.framework.services;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import uk.gov.hmcts.framework.business.vos.CsValueObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * <p>
 * Title: XML Services.
 * </p>
 * <p>
 * Description: Insulates the application components from knowedge of XML Services.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Faisal Shoukat
 * @version 1.0
 */
/*
 * Ref Date Author Description
 * 
 * 03-03-2003 AW Daley Interface changed to provide support for XML transformations
 */
public interface XmlServices {

    /**
     * createDocFromString.
     * 
     * @param xmlContent String
     * @return Document
     */
    Document createDocFromString(String xmlContent);

    /**
     * Retrieves the value of an xpath from an xml String.
     * 
     * @param xmlString The xml to search
     * @param xpath The xpath to look for
     * @return The value of the xpath
     */
    String getXpathValueFromXmlString(String xmlString, String xpath);
    
    /**
     * Convert a object implementing the CSValueObject interface to an xml document.
     * 
     * @param obj To work effectively the CSValueObject requires a default public constructor and
     *        public get methods for all vos that are required in the resulting xml document
     * @return Document xml representation of the CSValueObject
     */
    Document createDocFromValue(CsValueObject obj);

    /**
     * Adds the specified element before the beforeTag in the passed document.
     * 
     * @param document Document to which the element is added
     * @param tagName Name of the element that is added
     * @param value Node value of the element
     * @param beforeTag Tag before whish the new element is added
     */
    void addElementByTagName(Document document, String tagName, String value,
        String beforeTag);

    /**
     * Create a serializer to write to the output stream.
     * 
     * @param out the stream to create the serializer from
     * @return the new serializer
     * @throws IOException if an error occures creating the content handler
     */
    ContentHandler createXmlSerializer(OutputStream out) throws IOException;

    /**
     * Create a serializer to write to the character stream.
     * 
     * @param writer the character stream to create the serializer from
     * @return the new serializer
     * @throws IOException if an error occures creating the content handler
     */
    ContentHandler createXmlSerializer(Writer writer) throws IOException;

    /**
     * Create a serializer to write to the output stream.
     * 
     * @param out the stream to create the serializer from
     * @return the new serializer
     * @throws IOException if an error occures creating the content handler
     */
    ContentHandler createHtmlSerializer(OutputStream out) throws IOException;

    /**
     * Create a serializer to write to the character stream.
     * 
     * @param writer the character stream to create the serializer from
     * @return the new serializer
     * @throws IOException if an error occures creating the content handler
     */
    ContentHandler createHtmlSerializer(Writer writer) throws IOException;
}
