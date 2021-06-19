open module geci.jamal.test {
    requires geci.jamal;
    requires geci.testengine;
    requires geci.engine;
    requires jamal.testsupport;
    requires org.junit.jupiter.api;
    requires geci.api;
    requires jamal.api;
    requires jamal.engine;
    requires jamal.tools;
    exports javax0.geci.jamal_test;
    exports javax0.geci.jamal_test.unittestproxy;
    exports javax0.geci.jamal_test.util;
    exports javax0.geci.jamal_test.sample;
}