package uk.gov.hmcts.framework.services.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Entity resolver based on system id.
 * 
 * @author pznwc5
 */
public class SystemIdEntityResolver implements EntityResolver {

    /**
     * The method loads resource from the classpath using the system is.
     * 
     * @param publicId Public id
     * @param systemId System id
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(systemId);
    }

}
