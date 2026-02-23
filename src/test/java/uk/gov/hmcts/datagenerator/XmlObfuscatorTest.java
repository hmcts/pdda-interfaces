package uk.gov.hmcts.datagenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD")
class XmlObfuscatorTest {

    private static final String TRUE = "Result is true";
    private static final String FALSE = "Result is false";
    private static final String NOT_EQUAL = "Result is not equal";
    private static final String NULL = "Result null";
    private File tempFile;

    @AfterEach
    void cleanup() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
            tempFile = null;
        }
    }

    /**
     * Helper: call a private static method by name and parameter types.
     * @throws SecurityException exception
     * @throws NoSuchMethodException exception
     * @throws InvocationTargetException exception
     * @throws IllegalArgumentException exception
     * @throws IllegalAccessException exception
     */
    private Object callPrivateStatic(String methodName, Class<?>[] paramTypes, Object... args) 
        throws NoSuchMethodException, SecurityException, IllegalAccessException, 
        IllegalArgumentException, InvocationTargetException {
        Method method = XmlObfuscator.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    /**
     * Build a w3c Document from a string.
     */
    private Document documentFromString(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }
    
    private void setPrivateStaticFinalFieldUsingUnsafe(String fieldName, Object value) 
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = XmlObfuscator.class.getDeclaredField(fieldName);
        field.setAccessible(true);

        Field unsafeField = null;
        try {
            unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
        } catch (NoSuchFieldException nsfe) {
            throw new RuntimeException("Unable to access Unsafe to modify final field", nsfe);
        }
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

        Object staticFieldBase = unsafe.staticFieldBase(field);
        long staticFieldOffset = unsafe.staticFieldOffset(field);

        unsafe.putObject(staticFieldBase, staticFieldOffset, value);
    }
    
    @Test
    void testMainSuccessfulExecution() throws Exception {

        String xml =
                "<Root>"
                + "<CitizenNameForename>OrigForename</CitizenNameForename>"
                + "<CitizenNameSurname>OrigSurname</CitizenNameSurname>"
                + "<CitizenNameRequestedName>OrigRequested</CitizenNameRequestedName>"
                + "<BirthDate>1999-09-09</BirthDate>"
                + "<PostCode>OLDPC</PostCode>"
                + "<OffenceStatement>OLD OFFENCE</OffenceStatement>"
                + "<ListNote>OLD NOTE</ListNote>"
                + "</Root>";

        File input = Files.createTempFile("xmlobf-main-input", ".xml").toFile();
        File output = Files.createTempFile("xmlobf-main-output", ".xml").toFile();

        try {
            Files.writeString(input.toPath(), xml);

            // Execute main normally (success path should NOT call System.exit)
            XmlObfuscator.main(new String[]{
                    input.getAbsolutePath(),
                    output.getAbsolutePath()
            });

            // Verify output file exists
            assertTrue(output.exists(), FALSE);
            assertTrue(output.length() > 0, FALSE);

            // Parse and verify obfuscation occurred
            Document parsed = (Document) callPrivateStatic(
                    "parseXml",
                    new Class<?>[]{File.class},
                    output
            );

            Element root = parsed.getDocumentElement();
            assertNotNull(root, NULL);

            assertEquals("2000-01-01",
                    root.getElementsByTagName("BirthDate").item(0).getTextContent(), NOT_EQUAL);

            assertEquals("CF10 4PB",
                    root.getElementsByTagName("PostCode").item(0).getTextContent(), NOT_EQUAL);

            assertEquals("An offence",
                    root.getElementsByTagName("OffenceStatement").item(0).getTextContent(), NOT_EQUAL);

            assertEquals("List Note",
                    root.getElementsByTagName("ListNote").item(0).getTextContent(), NOT_EQUAL);

            String requested =
                    root.getElementsByTagName("CitizenNameRequestedName")
                            .item(0).getTextContent();

            assertNotNull(requested, NULL);
            assertFalse(requested.isBlank(), TRUE);

        } finally {
            input.delete();
            output.delete();
        }
    }
    
    @Test
    void testObfuscateReplacesFieldsAndPreservesRequestedNameComposition() throws Exception {
        String xml =
                "<Root>"
                + "<CitizenNameForename>OriginalForename</CitizenNameForename>"
                + "<CitizenNameSurname>OriginalSurname</CitizenNameSurname>"
                + "<CitizenNameRequestedName>ShouldBeReplaced</CitizenNameRequestedName>"
                + "<BirthDate>1990-12-12</BirthDate>"
                + "<PostCode>OLD</PostCode>"
                + "<OffenceStatement>OLD OFFENCE</OffenceStatement>"
                + "<ListNote>OLD NOTE</ListNote>"
                + "</Root>";

        Document doc = documentFromString(xml);

        // call private static obfuscate(Document)
        callPrivateStatic("obfuscate", new Class<?>[]{Document.class}, doc);

        Element root = doc.getDocumentElement();

        String forename = getTextContent(root, "CitizenNameForename");
        
        // Forename and surname should be uppercase A-Z and length 6 or 7
        assertNotNull(forename, "forename should not be null");
        assertTrue(forename.length() >= 6 && forename.length() <= 7, "forename length should be 6 or 7");
        assertTrue(forename.matches("^[A-Z]{6,7}$"), "forename should be uppercase letters only");

        String surname = getTextContent(root, "CitizenNameSurname");
        
        assertNotNull(surname, "surname should not be null");
        assertTrue(surname.length() >= 6 && surname.length() <= 7, "surname length should be 6 or 7");
        assertTrue(surname.matches("^[A-Z]{6,7}$"), "surname should be uppercase letters only");

        String requested = getTextContent(root, "CitizenNameRequestedName");
        
        // Requested name should equal "forename surname"
        assertEquals(forename + " " + surname, requested, "RequestedName should be composed of forename + surname");

        String birthDate = getTextContent(root, "BirthDate");
        String postcode = getTextContent(root, "PostCode");
        String offence = getTextContent(root, "OffenceStatement");
        
        assertEquals("2000-01-01", birthDate, "BirthDate should be set to default");
        assertEquals("CF10 4PB", postcode, "PostCode should be set to default");
        assertEquals("An offence", offence, "OffenceStatement should be default");
        
        String listNote = getTextContent(root, "ListNote");
        
        assertEquals("List Note", listNote, "ListNote should be default");
    }

    @Test
    void testRandomUpperAlphaDeterministicWithCustomRng() throws Exception {
        // deterministic SecureRandom that returns predictable ints
        java.security.SecureRandom deterministic = new java.security.SecureRandom() {
            private int counter = 1;
            @Override
            public int nextInt(int bound) {
                int value = counter % bound;
                counter++;
                return value;
            }
        };

        // inject deterministic RNG using Unsafe helper
        setPrivateStaticFinalFieldUsingUnsafe("RNG", deterministic);

        // Now call the private static randomUpperAlpha(6,7)
        String result = (String) callPrivateStatic("randomUpperAlpha", new Class<?>[]{int.class, int.class}, 6, 7);

        // Sanity assertions (length & characters)
        assertNotNull(result, NULL);
        assertTrue(result.length() >= 6 && result.length() <= 7, "length should be 6 or 7");
        assertTrue(result.matches("^[A-Z]{6,7}$"), FALSE);

        // For strict determinism, compute expected using the same algorithm and our deterministic RNG:
        // We'll re-inject a new deterministic RNG with the same deterministic behaviour and 
        // compute expected string by calling the method again.
        java.security.SecureRandom deterministic2 = new java.security.SecureRandom() {
            private int counter = 1;
            @Override
            public int nextInt(int bound) {
                int value = counter % bound;
                counter++;
                return value;
            }
        };
        setPrivateStaticFinalFieldUsingUnsafe("RNG", deterministic2);

        // Build expected manually:
        // First nextInt( maxLen - minLen + 1 ) => nextInt(2) -> returns 1 -> length = minLen + 1 = 7
        // Then 7 calls to nextInt(26) returning 2,3,4,5,6,7,8 (because counter continues incrementing)
        char[] alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int len = 7;
        StringBuilder expected = new StringBuilder(len);
        // the deterministic2 counter starts again at 1 so the next ints for 
        // letters begin at counter=2 -> index = 2 etc.
        int startingIndexCounter = 2;
        for (int i = 0; i < len; i++) {
            int idx = (startingIndexCounter + i) % alpha.length;
            expected.append(alpha[idx]);
        }

        String result2 = (String) callPrivateStatic("randomUpperAlpha",
            new Class<?>[]{int.class, int.class}, 6, 7);
        assertEquals(expected.toString(), result2,
            "randomUpperAlpha should produce exact expected deterministic string");
    }

    @Test
    void testSetTextClearsChildrenAndSetsText() throws Exception {
        // Build a document with an element that has children
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element el = document.createElement("Test");
        el.appendChild(document.createTextNode("OLD"));
        Element child = document.createElement("Child");
        child.appendChild(document.createTextNode("CHILDVALUE"));
        el.appendChild(child);
        document.appendChild(el);

        // call private static setText(Element, String)
        callPrivateStatic("setText", new Class<?>[]{Element.class, String.class}, el, "NEWVALUE");

        // Now el should have a single text child equal to "NEWVALUE"
        assertEquals("NEWVALUE", el.getTextContent(), "Element text should be replaced with NEWVALUE");

        // The element should have no element children anymore (only the text node)
        Node firstChild = el.getFirstChild();
        assertNotNull(firstChild, "There should be a child node (text)");
        assertEquals(Node.TEXT_NODE, firstChild.getNodeType(), "Remaining child should be a text node");
        assertEquals("NEWVALUE", firstChild.getNodeValue(), "text node value should be NEWVALUE");
        assertNull(firstChild.getNextSibling(), "There should be only one child (no siblings)");
    }

    @Test
    void testWriteXmlAndParseXmlRoundTrip() throws Exception {
        // make a small document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element root = document.createElement("RootElement");
        Element child = document.createElement("Child");
        child.appendChild(document.createTextNode("VAL"));
        root.appendChild(child);
        document.appendChild(root);

        // create temp file
        tempFile = Files.createTempFile("xmlobf-test", ".xml").toFile();

        // call private static writeXml(Document, File)
        callPrivateStatic("writeXml", new Class<?>[]{Document.class, File.class}, document, tempFile);

        assertTrue(tempFile.exists(), "Written XML file should exist");
        assertTrue(tempFile.length() > 0, "Written XML file should not be empty");

        // call private static parseXml(File)
        Document parsed = (Document) callPrivateStatic("parseXml", new Class<?>[]{File.class}, tempFile);

        assertNotNull(parsed.getDocumentElement(), "Parsed document should have a root element");
        assertEquals("RootElement", parsed.getDocumentElement().getNodeName(), "Root element name should match");
        assertEquals("VAL", parsed.getDocumentElement().getElementsByTagName("Child").item(0).getTextContent(),
            "Child text should match");
    }

    // Utility to fetch direct child text content by tag name under root
    private String getTextContent(Element root, String tag) {
        Node node = root.getElementsByTagName(tag).item(0);
        return node == null ? null : node.getTextContent();
    }
}