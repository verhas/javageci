package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

public abstract class AbstractGenerator implements Generator {
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
            var global = Tools.getParameters(klass, mnemonic());
            if (global != null) {
                process(source,klass,global);
            }
        }
    }

    public abstract void process(Source source, Class<?> klass, CompoundParams global)throws Exception;
    public abstract String mnemonic();
}
