package javax0.geci.templated;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;

public class Triplet implements Context {
    public Source source;
    public Class<?> klass;
    public Segment segment;

    @Override
    public Context triplet(Source source, Class<?> klass, Segment segment) {
        this.source = source;
        this.klass = klass;
        this.segment = segment;
        return this;
    }

    @Override
    public Source source() {
        return source;
    }

    @Override
    public Class<?> klass() {
        return klass;
    }

    @Override
    public Segment segment() {
        return segment;
    }
}
