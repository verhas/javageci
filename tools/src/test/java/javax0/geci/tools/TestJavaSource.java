package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestJavaSource {

    @Test
    public void testSourceBuilder() {
        var source = new JavaSource();
        var className = "MyClass";
        try (var klass = source.open("public class %s ", className)) {
            klass.write("private final String z;")
                .newline();
            try (var constructor = klass.open("public %s(String z)", className)) {
                constructor.write("this.z = z;");
            }
            klass.newline();
            try (var method = klass.open("public void static main(String[] args)")) {
                method.comment("this is some comment")
                    .statement("var z = new StringBuilder()")
                    .statement("var i = 0");
                try (var whBl = method.whileStatement("i<10")) {
                    try (var ifBl = whBl.ifStatement(" z.length() == 0 ")) {
                        ifBl.statement("z.append(\"a\")")
                            .elseStatement()
                            .statement("z.append(\"b\")");
                    }
                    whBl.statement("i ++");
                }
                method.statement("System.out.println(z.toString()");
            }
        }
        Assertions.assertEquals("public class MyClass {\n" +
            "    private final String z;\n" +
            "\n" +
            "    public MyClass(String z){\n" +
            "        this.z = z;\n" +
            "    }\n" +
            "\n" +
            "    public void static main(String[] args){\n" +
            "        // this is some comment\n" +
            "        var z = new StringBuilder();\n" +
            "        var i = 0;\n" +
            "        while( i<10 ){\n" +
            "            if(  z.length() == 0  ){\n" +
            "                z.append(\"a\");\n" +
            "            }else{\n" +
            "                z.append(\"b\");\n" +
            "            }\n" +
            "            i ++;\n" +
            "        }\n" +
            "        System.out.println(z.toString();\n" +
            "    }\n" +
            "}\n",source.toString());
    }
}
