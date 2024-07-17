package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.SerializationUtils;
import org.castor.core.util.Base64Decoder;
import org.castor.core.util.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.events.PublicDisplayEvent;

/**
 * <p>
 * Title: XhibitPddaEncoderHelper.
 * </p>
 * <p>
 * Description: Holds the methods used to encode, decode, serialize and deserialize events
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 * @version 1.0
 */
public abstract class XhibitPddaEncoderHelper extends XhibitPddaHelper {
    private static final Logger LOG = LoggerFactory.getLogger(XhibitPddaEncoderHelper.class);

    protected XhibitPddaEncoderHelper(EntityManager entityManager) {
        super(entityManager);
    }

    // Junit constructor
    protected XhibitPddaEncoderHelper(EntityManager entityManager,
        XhbConfigPropRepository xhbConfigPropRepository) {
        super(entityManager, xhbConfigPropRepository);
    }

    public static String serializePublicEvent(PublicDisplayEvent event) {
        LOG.debug("Serializing event: {}", event);
        byte[] byteArray = SerializationUtils.serialize(event);
        return new String(byteArray, java.nio.charset.StandardCharsets.ISO_8859_1);
    }

    public static PublicDisplayEvent deserializePublicEvent(String eventString) {
        if (eventString != null && !EMPTY_STRING.equals(eventString)) {
            LOG.debug("Deserializing event: {}", eventString);
            return (PublicDisplayEvent) SerializationUtils
                .deserialize(eventString.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        }
        return null;
    }

    public static String encodePublicEvent(String event) {
        LOG.debug("Encoding event: {}", event);
        return new String(Base64Encoder.encode(event.getBytes()));
    }

    public static String decodePublicEvent(String encodedEvent) {
        LOG.debug("Decoding event: {}", encodedEvent);
        return new String(Base64Decoder.decode(encodedEvent));
    }
}
