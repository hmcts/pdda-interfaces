package uk.gov.hmcts.pdda.business.services.validation.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.File;

/**
 * Resolves the entities name to a file in the entities dir. This uses the last part of the url
 * following a / or a space! The later is to support the current names of schemas.
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class FileEntityResolver implements EntityResolver {
    /**
     * The class's logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileEntityResolver.class);

    /**
     * The dir containing the entitites.
     */
    public File entityDir;

    public FileEntityResolver() {
        setEntityDir(null);
    }

    /**
     * Construct a new instance to resolve entities to files in the specified dir.
     * 
     * @param entityDir the dir containing the entitites.
     */
    public FileEntityResolver(final String entityDir) {
        this(entityDir == null ? null : new File(entityDir));
    }

    /**
     * Construct a new instance to resolve entities to files in the specified dir.
     * 
     * @param entityDir the dir containing the entitites.
     */
    public FileEntityResolver(final File entityDir) {
        if (entityDir == null) {
            throw new IllegalArgumentException("pEntityDir: null");
        }
        setEntityDir(entityDir);
    }


    /**
     * Resolve the entity.
     * 
     * @param publicId Public id
     * @param systemId System id
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        LOG.debug("Resolving entity {}", systemId);
        InputSource inputSource = new InputSource(
            Thread.currentThread().getContextClassLoader().getResourceAsStream(systemId));
        inputSource.setPublicId(publicId);
        return inputSource;
    }
    
    private void setEntityDir(File entityDir) {
        this.entityDir = entityDir;
    }
}
