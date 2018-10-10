package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

public abstract class AbstractGenerator extends AbstractGeneratorEx {

    public void processEx(Source source) throws Exception {
        final var klass = source.getKlass();
        if (klass != null) {
            var global = Tools.getParameters(klass, mnemonic());
            if (global != null) {
                process(source,klass,global);
            }
        }
    }

    public abstract void process(Source source, Class<?> klass, CompoundParams global)throws Exception;
    public abstract String mnemonic();
}
