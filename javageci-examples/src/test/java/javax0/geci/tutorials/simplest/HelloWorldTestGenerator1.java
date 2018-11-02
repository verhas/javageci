// START SNIPPET HelloWorldTestGenerator1
package javax0.geci.tutorials.simplest;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGenerator;
import javax0.geci.tools.CompoundParams;

public class HelloWorldTestGenerator1 extends AbstractGenerator {
    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try(var segment = source.open("HelloWorldTest")){
            segment.write_r("private static String greeting(){");
            segment.write("return \"greetings\";");
            segment.write_r("}");
        }
    }

    @Override
    public String mnemonic() {
        return "HelloWorldTest";
    }
}
// END SNIPPET