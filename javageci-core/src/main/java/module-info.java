module geci.core {
    requires geci.annotation;
    requires geci.api;
    requires geci.core.annotations;
    requires geci.tools;

    exports javax0.geci.accessor;
    exports javax0.geci.annotationbuilder;
    exports javax0.geci.builder;
    exports javax0.geci.config;
    exports javax0.geci.cloner;
    exports javax0.geci.delegator;
    exports javax0.geci.equals;
    exports javax0.geci.factory;
    exports javax0.geci.fluent;
    exports javax0.geci.iterate;
    exports javax0.geci.mapper;
    exports javax0.geci.record;
    exports javax0.geci.repeated;
    exports javax0.geci.templated;
}
