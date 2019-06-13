package javax0.geci.templated;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;

public interface Context {
    Context triplet(Source source,
                    Class<?> klass,
                    Segment segment);

    Source source();

    Class<?> klass();

    Segment segment();
}
