// snippet BeanGenerator_head_00
package javax0.geci.tutorials.beangenerator;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static javax0.geci.api.Source.Set.set;
import static javax0.geci.tools.CaseTools.ucase;

public class BeanGenerator extends AbstractGeneratorEx {
// end snippet

    // snippet BeanGenerator_main1_01
    @Override
    public void processEx(Source source) throws Exception {
        if (source.getAbsoluteFile().endsWith(".xml")) {
//          ...
// end snippet
// snippet BeanGenerator_main2
            final var newKlass = source.getKlassSimpleName();
            final var pckage = source.getPackageName();
            final var target = source.newSource(set("java"), newKlass + ".java");
            final var doc = getDocument(source);
// end snippet
// snippet BeanGenerator_main3
            try (final var segment = target.open()) {
                segment.write("package " + pckage + ";");
                segment.write_r("public class " + newKlass + " {");
                var fields = doc.getElementsByTagName("field");
                for (var index = 0; index < fields.getLength(); index++) {
                    var field = fields.item(index);
                    var attributes = field.getAttributes();
                    String name = attributes.getNamedItem("name").getNodeValue();
                    String type = attributes.getNamedItem("type").getNodeValue();
                    segment.write("private " + type + " " + name + ";");

                    segment.write_r("public " + type + " get" + ucase(name) + "() {");
                    segment.write("return " + name + ";");
                    segment.write_l("}");

                    segment.write_r("public void set" + ucase(name) + "(" + type + " " + name + ") {");
                    segment.write("this." + name + " = " + name + ";");
                    segment.write_l("}");

                }
                segment.write_l("}");
            }
// end snippet
// snippet BeanGenerator_main1_02
        }
    }
// end snippet

    // snippet BeanGenerator_aux
    private Document getDocument(Source source) throws ParserConfigurationException, SAXException, IOException {
        final var dbFactory = DocumentBuilderFactory.newInstance();
        final var dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(source.toString())));
    }
    //end snippet
// snippet BeanGenerator_head_01
}
// end snippet