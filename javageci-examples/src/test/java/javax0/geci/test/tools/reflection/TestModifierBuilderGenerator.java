package javax0.geci.test.tools.reflection;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.tools.GeciReflectionTools;

public class TestModifierBuilderGenerator implements Generator {
    private static final String[] a = new String[]{
            "Modifier.PRIVATE",
            "Modifier.PROTECTED",
            "Modifier.PUBLIC",
            "Modifier.FINAL",
            "Modifier.STATIC",
            "Modifier.SYNCHRONIZED"
    };
    private static final String[] b = new String[]{
            "private ",
            "protected ",
            "public ",
            "final ",
            "static ",
            "synchronized "
    };

    @Override
    public void process(Source source) {
        try {
            process0(source);
        } catch (Exception e) {
            throw new GeciException(e);
        }
    }

    private void process0(Source source) throws Exception {
        final var klass = source.getKlass();
        if (klass != null) {
            var global = GeciReflectionTools.getParameters(klass, "TestModifierBuilderGenerator");
            if (global != null) {
                try (var segment = source.open("allTests")) {
                    segment.write("assertEquals(new ModifiersBuilder(0).toString(), \"\");");
                    for (int i = 1; i < 64; i++) {
                        var j = i;
                        var k = 0;
                        StringBuilder aa = new StringBuilder();
                        StringBuilder bb = new StringBuilder();
                        var sep = "";
                        while (j > 0) {
                            if (j % 2 > 0) {
                                aa.append(sep).append(a[k]);
                                sep = " | ";
                                bb.append(b[k]);
                            }
                            j = j / 2;
                            k++;
                        }
                        segment.write("assertEquals(new ModifiersBuilder(" + aa + ").toString(), \"" + bb + "\");");
                    }
                }
            }
        }
    }
}
