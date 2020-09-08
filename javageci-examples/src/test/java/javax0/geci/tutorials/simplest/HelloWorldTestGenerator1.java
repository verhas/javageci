// snippet HelloWorldTestGenerator1
package javax0.geci.tutorials.simplest;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;

public class HelloWorldTestGenerator1 extends AbstractJavaGenerator {
    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try(var segment = source.open("HelloWorldTest")){
            segment.write_r("private static String greeting() {");
            segment.write("return \"greetings\";");
            segment.write_l("}");
        }
    }

    @Override
    public String mnemonic() {
        return "HelloWorldTest";
    }
}
//  end snippet