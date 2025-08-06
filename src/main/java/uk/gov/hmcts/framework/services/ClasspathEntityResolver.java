package uk.gov.hmcts.framework.services;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Title: ClasspathEntityResolver.


 * Description: This class enables us to have the dtd referenced in the mapping.xml file to be
 * located on the classpath relative location


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Faisal Shoukat
 * @version 1.0
 */

@SuppressWarnings("PMD.LawOfDemeter")
public final class ClasspathEntityResolver implements EntityResolver {
    private static final ClasspathEntityResolver THIS = new ClasspathEntityResolver();

    private final Map<String, String> dtdMap;

    /**
     * constructor made private to enforce singleton.
     */
    private ClasspathEntityResolver() {
        dtdMap = new ConcurrentHashMap<>();
    }

    /**
     * register a new entity reference for key use the public id and for the value use the classpath
     * relative location.
     */
    public void registerReference(String publicId, String location) {
        dtdMap.put(publicId, location);
    }

    /**
     * implement resolve entity method from super class.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        String dtdUrl = null;
        if (publicId != null) {
            // find the registered dtd url
            dtdUrl = dtdMap.get(publicId);
        }
        if (dtdUrl == null) {
            return null;
        }
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dtdUrl);

        return new InputSource(stream);
    }

    public static ClasspathEntityResolver getInstance() {
        return THIS;
    }

}
