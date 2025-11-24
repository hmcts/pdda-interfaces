package uk.gov.hmcts.pdda.business.services.pdda;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;

import java.util.Base64;

/**
 * Title: PddaSerializationUtils.
 * Description: Holds the methods used to encode, decode, serialize and deserialize events
 * Copyright: Copyright (c) 2024
 * Company: CGI
 * @author Luke Gittins
 * @version 1.0
 */
@SuppressWarnings({"PMD", "squid:S3776"})
public final class PddaSerializationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PddaSerializationUtils.class);

    private PddaSerializationUtils() {
        // Private constructor
    }

    public static byte[] serializePublicEvent(PublicDisplayEvent event) {
        LOG.debug("Serializing event: {}", event);
        return SerializationUtils.serialize(event);
    }

    public static PublicDisplayEvent deserializePublicEvent(byte[] eventBytes) {
        if (eventBytes == null) {
            return null;
        }

        /*try {
            // --- Hash & temp file for byte verification ---
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(eventBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            LOG.debug("Deserializing event: bytes.length={}, sha256={}", eventBytes.length,
                sb.toString());

            File tmp = File.createTempFile("publicnotice-", ".ser");
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                fos.write(eventBytes);
            }
            LOG.debug("Wrote decoded bytes to temporary file: {}", tmp.getAbsolutePath());
        } catch (Throwable t) {
            LOG.warn("Could not compute hash / write temp file for event bytes", t);
        }*/

        // --- Actual deserialization ---
        PublicDisplayEvent evt = null;
        try {
            evt = (PublicDisplayEvent) SerializationUtils.deserialize(eventBytes);
        } catch (Throwable t) {
            LOG.error("Deserialization failed", t);
            return null;
        }

        // --- Inspect immediately AFTER deserialization ---
        try {
            if (evt == null) {
                LOG.debug("AFTER DESERIALIZE: event is NULL");
                return null;
            }

            Object courtRoomIdentifier = null;

            // Prefer a typed getter, fallback to reflection if getter missing
            try {
                // If the class has a getCourtRoomIdentifier() method, use it.
                // avoid compile-time dependency by reflection (works in runtime)
                java.lang.reflect.Method getter =
                    evt.getClass().getMethod("getCourtRoomIdentifier");
                courtRoomIdentifier = getter.invoke(evt);
                LOG.debug("AFTER DESERIALIZE: used getter getCourtRoomIdentifier()");
            } catch (NoSuchMethodException nsme) {
                // fallback: try to read field directly
                try {
                    java.lang.reflect.Field criField =
                        evt.getClass().getDeclaredField("courtRoomIdentifier");
                    criField.setAccessible(true);
                    courtRoomIdentifier = criField.get(evt);
                    LOG.debug(
                        "AFTER DESERIALIZE: used reflection to read courtRoomIdentifier field");
                } catch (NoSuchFieldException | IllegalAccessException inner) {
                    LOG.debug(
                        "AFTER DESERIALIZE: courtRoomIdentifier not found via getter or field: {}",
                        inner.toString());
                }
            } catch (Throwable t) {
                LOG.warn("AFTER DESERIALIZE: error invoking getCourtRoomIdentifier()", t);
            }

            if (courtRoomIdentifier == null) {
                LOG.debug("AFTER DESERIALIZE: courtRoomIdentifier = NULL");
            } else {
                Class<?> criClass = courtRoomIdentifier.getClass();
                LOG.debug("AFTER DESERIALIZE: CourtRoomIdentifier class: {}", criClass.getName());
                LOG.debug("AFTER DESERIALIZE: CourtRoomIdentifier classloader: {}",
                    criClass.getClassLoader());

                try {
                    java.lang.reflect.Field f = criClass.getDeclaredField("publicNotices");
                    f.setAccessible(true);
                    Object arrObj = f.get(courtRoomIdentifier);
                    if (arrObj == null) {
                        LOG.debug("AFTER DESERIALIZE: publicNotices = NULL");
                    } else {
                        int len = java.lang.reflect.Array.getLength(arrObj);
                        Class<?> comp = arrObj.getClass().getComponentType();
                        LOG.debug(
                            "AFTER DESERIALIZE: publicNotices array length = {}, componentType = {},"
                            + "component classloader = {}",
                            len, comp.getName(), comp.getClassLoader());
                        // print up to first 10 elements
                        for (int i = 0; i < Math.min(len, 10); i++) {
                            Object el = java.lang.reflect.Array.get(arrObj, i);
                            LOG.debug("AFTER DESERIALIZE:   [{}] class={} toString={}", i,
                                el == null ? "null" : el.getClass().getName(), el);
                        }
                    }
                } catch (NoSuchFieldException nsf) {
                    LOG.debug(
                        "AFTER DESERIALIZE: publicNotices field not declared on CourtRoomIdentifier: {}",
                        nsf.toString());
                } catch (Throwable t) {
                    LOG.error("AFTER DESERIALIZE: Error inspecting publicNotices via reflection",
                        t);
                }
            }
        } catch (Throwable t) {
            LOG.warn("AFTER DESERIALIZE: unexpected inspection error", t);
        }

        return evt;
    }



    public static String encodePublicEvent(byte[] event) {
        LOG.debug("Encoding event: {}", event);
        return new String(Base64.getEncoder().encode(event));
    }

    public static byte[] decodePublicEvent(String encodedEvent) {
        LOG.debug("Decoding event: {}", encodedEvent);
        return Base64.getDecoder().decode(encodedEvent);
    }
}
