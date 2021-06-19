import javax0.geci.jamal.junit5.Engine;

module geci.testengine {
    requires geci.jamal;
    requires geci.tools;
    requires geci.api;
    requires geci.engine;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    exports javax0.geci.jamal.junit5;
    provides org.junit.platform.engine.TestEngine with Engine;
}