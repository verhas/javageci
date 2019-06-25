package javax0.geci.annotations;

import java.io.File;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

public class AnnotationBuilder  extends AbstractJavaGenerator {


    private static class Config {
        protected String in = null;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try {
            final var implementedKeys = GeciReflectionTools.getMethod(klass, "implementedKeys");
            final var mnemonic = GeciReflectionTools.getMethod(klass, "mnemonic");

            final var in = global.get("in", () -> klass.getProtectionDomain().getCodeSource().getLocation().getPath());

            final var fileName = (String) mnemonic.invoke(klass.getDeclaredConstructor().newInstance());

            File file = new File(in.replace(".", "/"), fileName);
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getCanonicalPath());
            file.createNewFile();
        } catch (NoSuchMethodException ex) {
            throw new GeciException("The annotation builder can not be called on " + klass.getName() + ", because it doesn't have a required method.");
        }
    }

    //<editor-fold id="configBuilder">

    //</editor-fold>
}
