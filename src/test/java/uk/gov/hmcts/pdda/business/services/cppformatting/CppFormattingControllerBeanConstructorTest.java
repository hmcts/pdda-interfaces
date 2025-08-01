package uk.gov.hmcts.pdda.business.services.cppformatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**

 * Title: Cpp List Controller Bean Constructor Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class CppFormattingControllerBeanConstructorTest {

    private static final String NOT_INSTANCE = "Result is not an Instance";

    @TestSubject
    private final CppFormattingControllerBean classUnderTest =
        new CppFormattingControllerBean(EasyMock.createMock(EntityManager.class));

    @Test
    void testGetPublicDisplayNotifier() {
        assertInstanceOf(PublicDisplayNotifier.class, classUnderTest.getPublicDisplayNotifier(),
            NOT_INSTANCE);
    }
}
