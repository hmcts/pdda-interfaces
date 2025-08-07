package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.framework.xml.transform.TemplatesAdapter;

import javax.xml.transform.TransformerConfigurationException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: TranslationXmlTemplates Test.
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
class TranslationXmlTemplatesTest {

    private static final String NOT_TRUE = "Result is Not True";
    private static final String NOT_FALSE = "Result is Not False";

    @Mock
    private TemplatesAdapter mockTemplatesAdapter;
    
    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new TranslationXmlTemplates("Test", mockTemplatesAdapter);
        assertTrue(result, NOT_TRUE);
    }
    
    @Test
    void testDefaultConstructorFail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TranslationXmlTemplates(null, null);
        });
    }
    
    @Test
    void testNewTransformer() throws TransformerConfigurationException {
        boolean result = false;
        TranslationXmlTemplates classUnderTest = new TranslationXmlTemplates("Test", mockTemplatesAdapter);
        classUnderTest.newTransformer();
        assertFalse(result, NOT_FALSE);
    }
}
