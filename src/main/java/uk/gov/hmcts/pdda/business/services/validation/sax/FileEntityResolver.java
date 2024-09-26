package uk.gov.hmcts.pdda.business.services.validation.sax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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
        String newSystemId = resolveEntityUrl(systemId);
        if (newSystemId != null) {
            InputSource inputSource = new InputSource(newSystemId);
            inputSource.setPublicId(publicId);
            return inputSource;
        }
        return null;
    }

    /**
     * Resolve the entity URL.
     * 
     * @param publicId String
     */
    protected String resolveEntityUrl(final String publicId) {
        File entityFile = resolveEntityFile(publicId);
        if (entityFile != null) {
            try {
                String entityUrl = entityFile.toURL().toExternalForm();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved \"" + publicId + "\" to url \"" + entityUrl + "\".");
                }
                return entityUrl;
            } catch (final MalformedURLException e) {
                LOG.warn("An error ocured creating URL for file \"" + entityFile.getAbsolutePath()
                    + "\".", e);
                return null;
            }
        }
        return null;
    }

    /**
     * Resolve the entity.
     * 
     * @param publicId String
     */
    protected File resolveEntityFile(final String publicId) {
        if (publicId == null) {
            throw new IllegalArgumentException("publicId: null");
        }

        URL resource = Thread.currentThread().getContextClassLoader().getResource(publicId);
        if (resource == null) {

            resource = Thread.currentThread().getContextClassLoader().getResource(publicId);

            if (resource == null) {
                throw new IllegalArgumentException("file " + publicId + " is not found");
            }
        }

        return new File(resource.getFile());
    }
    
    private void setEntityDir(File entityDir) {
        this.entityDir = entityDir;
    }
}
