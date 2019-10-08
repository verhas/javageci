package javax0.geci.tutorials.hello;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

public class HelloWorldGenerator1 implements Generator {
    public void process(Source source) {
        try (final var segment = source.open("hello")) {
            segment.write_r("public static void hello(){");
            segment.write("System.out.println(\"Hello, World\");");
            segment.write_l("}");
        } catch (Exception e) {
            throw new GeciException(e);
        }
    }
}
