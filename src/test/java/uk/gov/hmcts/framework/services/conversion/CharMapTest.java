package uk.gov.hmcts.framework.services.conversion;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 * Title: CharMapTest.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class CharMapTest {

    private static final String EQUAL = "Result is not equal";

    @TestSubject
    private final CharMap classUnderTest = new CharMap(new BasicConverter());

    @BeforeEach
    public void setUp() {
        Map<String, Serializable> map = new ConcurrentHashMap<>();
        map.put("Valid", "V");
        map.put("Invalid", "Invalid");
        ReflectionTestUtils.setField(classUnderTest, "map", map);
    }

    @Test
    void testGetCharValid() {
        assertEquals('V', classUnderTest.getChar("Valid"), EQUAL);
    }

    @Test
    void testGetCharInvalid() {
        Assertions.assertThrows(ValueConvertException.class, () -> {
            classUnderTest.getChar("Invalid");
        });
    }
}
