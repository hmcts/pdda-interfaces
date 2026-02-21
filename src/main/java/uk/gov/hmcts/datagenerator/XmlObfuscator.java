package uk.gov.hmcts.datagenerator;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.security.SecureRandom;

public final class XmlObfuscator {

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final String DEFAULT_BIRTHDATE = "2000-01-01";
    private static final String DEFAULT_POSTCODE = "CF10 4PB";
    private static final String DEFAULT_OFFENCE_STATEMENT = "An offence";
    private static final String DEFAULT_LIST_NOTE = "List Note";

    private XmlObfuscator() {}

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java XmlObfuscator <input.xml> <output.xml>");
            System.exit(2);
        }

        File input = new File(args[0]);
        File output = new File(args[1]);

        try {
            Document doc = parseXml(input);
            obfuscate(doc);
            writeXml(doc, output);
            System.out.println("Obfuscated XML written to: " + output.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to obfuscate XML: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static Document parseXml(File input) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try { dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); } catch (Exception ignored) {}
        try { dbf.setFeature("http://xml.org/sax/features/external-general-entities", false); } catch (Exception ignored) {}
        try { dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false); } catch (Exception ignored) {}

        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(input);
    }

    private static void obfuscate(Document doc) {
        NameState state = new NameState();
        Element root = doc.getDocumentElement();
        if (root != null) {
            traverse(root, state);
        }
    }

    private static void traverse(Node node, NameState state) {

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element el = (Element) node;
            String local = el.getLocalName();

            if (local == null) {
                local = el.getNodeName();
                int colon = local.indexOf(':');
                if (colon >= 0) local = local.substring(colon + 1);
            }

            switch (local) {

                case "CitizenNameForename":
                    String newForename = randomUpperAlpha(6, 7);
                    setText(el, newForename);
                    state.lastForename = newForename;
                    break;

                case "CitizenNameSurname":
                    String newSurname = randomUpperAlpha(6, 7);
                    setText(el, newSurname);
                    state.lastSurname = newSurname;
                    break;

                case "CitizenNameRequestedName":
                    String f = state.lastForename != null ? state.lastForename : randomUpperAlpha(6, 7);
                    String s = state.lastSurname != null ? state.lastSurname : randomUpperAlpha(6, 7);
                    setText(el, f + " " + s);
                    break;

                case "BirthDate":
                    setText(el, DEFAULT_BIRTHDATE);
                    break;

                case "PostCode":
                    setText(el, DEFAULT_POSTCODE);
                    break;

                case "OffenceStatement":
                    setText(el, DEFAULT_OFFENCE_STATEMENT);
                    break;

                case "ListNote":
                    setText(el, DEFAULT_LIST_NOTE);
                    break;

                default:
                    // do nothing
            }
        }

        Node child = node.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            traverse(child, state);
            child = next;
        }
    }

    private static void setText(Element el, String value) {
        while (el.hasChildNodes()) {
            el.removeChild(el.getFirstChild());
        }
        el.appendChild(el.getOwnerDocument().createTextNode(value));
    }

    private static String randomUpperAlpha(int minLen, int maxLen) {
        int len = minLen + RNG.nextInt((maxLen - minLen) + 1);
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = ALPHA[RNG.nextInt(ALPHA.length)];
        }
        return new String(buf);
    }

    private static void writeXml(Document doc, File output) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // DO NOT force indentation (prevents newlines being added everywhere)
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        transformer.transform(new DOMSource(doc), new StreamResult(output));
    }

    private static final class NameState {
        String lastForename;
        String lastSurname;
    }
}
