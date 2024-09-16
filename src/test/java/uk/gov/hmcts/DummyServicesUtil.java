package uk.gov.hmcts;

import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.vos.translation.impl.LookupTranslationBundle;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DummyServicesUtil {

    private DummyServicesUtil() {
        // Do nothing
    }

    public static Integer getRandomNumber() {
        return Double.valueOf(Math.random()).intValue();
    }

    public static Integer getDummyId() {
        return -99;
    }

    public static Long getDummyLongId() {
        return Long.valueOf(getDummyId());
    }

    public static <T> List<T> getNewArrayList() {
        return new ArrayList<>();
    }

    public static ByteArrayInputStream getByteArrayInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static LookupTranslationBundle getDummyLookupTranslationBundle(Locale locale) {
        LookupTranslationBundle result = new LookupTranslationBundle(locale);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            result.addTranslation(null, null, null, false);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            result.addTranslation("", null, null, false);
        });
        result.getTranslation("");
        result.getTranslation("", "");
        result.addTranslation("test", "test", "", false);
        result.getTranslation("test", "");
        return result;
    }

    public static XhbConfigPropDao getXhbConfigPropDao(String propertyName, String propertyValue) {
        Integer configPropId = getRandomNumber();
        return new XhbConfigPropDao(configPropId, propertyName, propertyValue);
    }
}
