package javax0.geci.consistency;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple XML handling facade. This class is used in the test {@code
 * javax0.geci.consistency.DocumentationTest} that checks that the
 * documentation is consistent with the project files. It essentially
 * checks that the versions in the documentation are the same as in the
 * project files.
 */
public class Xml {
    final Document doc;
    final XPath xPath;

    /**
     * Get the object from a file.
     *
     * @param file that contains the XML content. This is the {@code
     * pom.xml} file
     * @return the new object that can be queried using xPath
     * @throws IOException if the file can not be read
     * @throws SAXException if the file has bad syntax
     * @throws ParserConfigurationException if he parser is not properly configured
     */
    public static Xml from(File file) throws IOException, SAXException, ParserConfigurationException {
        return new Xml(file);
    }

    private Xml(File file) throws ParserConfigurationException, IOException, SAXException {
        var dbFactory = DocumentBuilderFactory.newInstance();
        final var dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);
        xPath = XPathFactory.newInstance().newXPath();
    }

    /**
     * Get the strings that are at the xPath.
     *
     * @param path the path we use to fetch the strings
     * @return the list of the strings that can be found on the path
     * @throws XPathExpressionException if something happens during parsing
     */
    public List<String> gets(String path) throws XPathExpressionException {
        var nodes = (NodeList) xPath.evaluate(path, doc, XPathConstants.NODESET);
        var strings = new ArrayList<String>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); ++i) {
            var e = (Element) nodes.item(i);
            strings.add(e.getTextContent());
        }
        return strings;
    }

    /**
     * Get a single string that is at the xPath in the xml.
     * @param path the path we use to fetch the strings
     * @return the string that can be found on the path
     * @throws XPathExpressionException if something happens during parsing
     */
    public String get(String path) throws XPathExpressionException {
        return (String) xPath.evaluate(path + "/text()", doc, XPathConstants.STRING);
    }
}
