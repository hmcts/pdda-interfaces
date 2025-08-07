package uk.gov.hmcts.pdda.business.services.formatting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.framework.xml.transform.TemplatesAdapter;

import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Title: SaxUtils Test.
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SaxUtilsTest {

    private static final String NOT_TRUE = "Result is Not True";

    @Mock
    private TemplatesAdapter mockTemplatesAdapter;

    @Mock
    private XslServices mockXslServices;
    
    @Mock
    private XMLFilterImpl mockXmlFilterImpl;

    @Mock
    private Reader mockReader;

    @Test
    void testCreateSource()
        throws TransformerException, SAXException, ParserConfigurationException, IOException {
        // Setup
        XMLFilter[] filters = {mockXmlFilterImpl, mockXmlFilterImpl};
        TemplatesAdapter[] templates = {mockTemplatesAdapter, mockTemplatesAdapter};
        Mockito.when(mockXslServices.getFilters(templates))
            .thenReturn(filters);
        boolean result = true;
        // Run
        SaxUtils.createSource(mockXslServices, mockReader, templates);
        // Checks
        assertTrue(result, NOT_TRUE);
    }
}
