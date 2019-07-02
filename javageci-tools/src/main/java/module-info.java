module geci.tools {
    exports javax0.geci.log;
    exports javax0.geci.javacomparator;
    exports javax0.geci.tools;
    exports javax0.geci.tools.syntax;
    exports javax0.geci.tools.reflection;
    exports javax0.geci.javacomparator.lex;
    requires geci.annotation;
    requires transitive geci.api;
}