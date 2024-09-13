package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * <p>
 * Title: Cpp List Controller Bean Constructor Test.
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
class CppFormattingControllerBeanConstructorTest {

    private static final String NOT_INSTANCE = "Result is not an Instance";

    @Mock
    private EntityManager mockEntityManager;

    @TestSubject
    private final CppFormattingControllerBean classUnderTest = new CppFormattingControllerBean(mockEntityManager);

    @Test
    void testGetPublicDisplayNotifier() {
        assertInstanceOf(PublicDisplayNotifier.class,
            classUnderTest.getPublicDisplayNotifier(), NOT_INSTANCE);
    }
}
