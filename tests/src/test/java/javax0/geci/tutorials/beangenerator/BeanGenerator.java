// START SNIPPET BeanGenerator
package javax0.geci.tutorials.beangenerator;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

import java.io.IOException;
import java.nio.file.Paths;

import static javax0.geci.api.Source.Set.set;

public class BeanGenerator implements Generator {

    @Override
    public void process(Source source) {
        try {
            if (source.getAbsoluteFile().endsWith(".xml")) {
                var newFile = Paths.get(source.getAbsoluteFile())
                        .getFileName().toString().replaceAll(".xml$", ".java");
                var target = source.newSource(set("java"), newFile);
                try (var segment = source.open("HelloWorldTest")) {
                    segment.write_r("private static String greeting(){");
                    segment.write("return \"greetings\";");
                    segment.write_r("}");
                }
            }
        } catch (IOException e) {
            throw new GeciException(e);
        }
    }
}
// END SNIPPET