package javax0.geci.tutorials.hello;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;

import java.io.IOException;

public class HelloWorldGenerator2 extends AbstractGeneratorEx {
    public void processEx(Source source) throws IOException {
        try( final var segment = source.open("hello")) {
            if (segment != null) {
                segment.write_r("public static void hello(){");
                segment.write("System.out.println(\"Hello, World\");");
                segment.write_l("}");
            }
        }
    }
}
