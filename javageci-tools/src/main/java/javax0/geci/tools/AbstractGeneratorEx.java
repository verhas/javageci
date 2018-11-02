package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

public abstract class AbstractGeneratorEx implements Generator {
    @Override
    public void process(Source source) {
        try {
            processEx(source);
        } catch (Exception e) {
            throw new GeciException(e);
        }
    }

    public abstract void processEx(Source source) throws Exception;
}
