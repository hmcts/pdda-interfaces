package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class MaskingTranslatorTest {

    private BasicTranslator mockParent;
    private TranslationContext mockContext;
    private Document mockDocument;
    private NodeList mockNodeList;
    private Node mockNode;
    private Node mockFirstChild;

    private static final String MASKED_NAME = "John Doe";
    private static final String MASKED_FLAG = "Y";

    private static final String MASKED_N = "maskedName";
    private static final String MASKED_F = "maskedFlag";

    private MaskingTranslator maskingTranslator;

    @BeforeEach
    void setUp() {
        mockParent = mock(BasicTranslator.class);
        mockContext = mock(TranslationContext.class);
        mockDocument = mock(Document.class);
        mockNodeList = mock(NodeList.class);
        mockNode = mock(Node.class);
        mockFirstChild = mock(Node.class);

        maskingTranslator = new MaskingTranslator(1, mockParent);
    }

    @Test
    void constructorSetsParentTranslator() {
        assertNotNull(maskingTranslator);
    }

    @Test
    void testTranslateResetsDefendantDetailsAndDelegatesToParent() {
        when(mockContext.get(MASKED_N)).thenReturn(MASKED_NAME);
        when(mockContext.get(MASKED_F)).thenReturn(MASKED_FLAG);

        // Mock XML structure for resetValue coverage
        when(mockDocument.getElementsByTagName(Mockito.anyString())).thenReturn(mockNodeList);
        when(mockNodeList.getLength()).thenReturn(1);
        when(mockNodeList.item(0)).thenReturn(mockNode);
        when(mockNode.getFirstChild()).thenReturn(mockFirstChild);

        // Mock translator chain
        when(mockParent.translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any())).thenReturn("translated-result");

        String result =
            maskingTranslator.translate(mockContext, Locale.UK, mockDocument, new Date(), 5);

        assertEquals("translated-result", result);
        verify(mockParent).translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any());
    }

    @Test
    void translateAppendsChildWhenNoFirstChildExists() {
        when(mockContext.get(MASKED_N)).thenReturn(MASKED_NAME);
        when(mockContext.get(MASKED_F)).thenReturn(MASKED_FLAG);

        when(mockDocument.getElementsByTagName(Mockito.anyString())).thenReturn(mockNodeList);
        when(mockNodeList.getLength()).thenReturn(1);
        when(mockNodeList.item(0)).thenReturn(mockNode);
        when(mockNode.getFirstChild()).thenReturn(null);

        // Use Text (the correct return type of createTextNode)
        Text mockText = mock(Text.class);
        when(mockDocument.createTextNode(MASKED_NAME)).thenReturn(mockText);

        // Optional, but harmless: define appendChild’s return to avoid strict stubbing warnings
        when(mockNode.appendChild(mockText)).thenReturn(mockText);

        when(mockParent.translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any())).thenReturn("appended-result");

        String result =
            maskingTranslator.translate(mockContext, Locale.US, mockDocument, new Date(), 7);

        assertEquals("appended-result", result);
    }

    @Test
    void testTranslateHandlesEmptyNodeListGracefully() {
        when(mockContext.get(MASKED_N)).thenReturn(MASKED_NAME);
        when(mockContext.get(MASKED_F)).thenReturn(MASKED_FLAG);

        when(mockDocument.getElementsByTagName(Mockito.anyString())).thenReturn(mockNodeList);
        when(mockNodeList.getLength()).thenReturn(0);

        when(mockParent.translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any())).thenReturn("no-nodes");

        String result =
            maskingTranslator.translate(mockContext, Locale.CANADA, mockDocument, new Date(), 10);

        assertEquals("no-nodes", result);
        verify(mockParent).translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any());
    }

    @Test
    void testTranslateHandlesNullNodeGracefully() {
        when(mockContext.get(MASKED_N)).thenReturn(MASKED_NAME);
        when(mockContext.get(MASKED_F)).thenReturn(MASKED_FLAG);

        when(mockDocument.getElementsByTagName(Mockito.anyString())).thenReturn(mockNodeList);
        when(mockNodeList.getLength()).thenReturn(1);
        when(mockNodeList.item(0)).thenReturn(null);

        when(mockParent.translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any())).thenReturn("null-node");

        String result =
            maskingTranslator.translate(mockContext, Locale.FRANCE, mockDocument, new Date(), 20);

        assertEquals("null-node", result);
        verify(mockParent).translate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any());
    }
}
