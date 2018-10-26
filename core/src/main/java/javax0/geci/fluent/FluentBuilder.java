package javax0.geci.fluent;

import javax0.geci.fluent.internal.FluentBuilderImpl;

public interface FluentBuilder {
    static FluentBuilder from(Class<?> klass) {
        return new FluentBuilderImpl(klass);

    }

    FluentBuilder start(String method);

    FluentBuilder implement(String interfaces);

    FluentBuilder fluentType(String type);

    FluentBuilder exclude(String method);

    FluentBuilder cloner(String method);

    FluentBuilder optional(String method);

    FluentBuilder optional(FluentBuilder sub);

    FluentBuilder oneOrMore(String method);

    FluentBuilder oneOrMore(FluentBuilder sub);

    FluentBuilder zeroOrMore(String method);

    FluentBuilder zeroOrMore(FluentBuilder sub);

    FluentBuilder oneOf(String... methods);

    FluentBuilder oneOf(FluentBuilder... subs);

    FluentBuilder one(String method);

    FluentBuilder one(FluentBuilder builder);

    FluentBuilder name(String interfaceName);
}
