import javax0.geci.jamal.junit5.Engine;
import javax0.geci.jamal.macros.Cap;
import javax0.geci.jamal.macros.Contains;
import javax0.geci.jamal.macros.Equals;
import javax0.geci.jamal.macros.Fields;
import javax0.geci.jamal.macros.IfNotVoid;
import javax0.geci.jamal.macros.IfVoid;
import javax0.geci.jamal.macros.Methods;
import javax0.geci.jamal.macros.Replace;
import javax0.geci.jamal.macros.Set;

import static javax0.geci.jamal.macros.ArgumentFormatters.ArgList;
import static javax0.geci.jamal.macros.ArgumentFormatters.CallArgs;
import static javax0.geci.jamal.macros.ArgumentFormatters.ClassList;

module geci.jamal {
    requires transitive geci.engine;
    requires transitive geci.tools;
    requires transitive geci.api;
    requires transitive jamal.engine;
    requires transitive jamal.api;
    requires transitive jamal.tools;
    requires transitive org.junit.jupiter.engine;
    requires transitive org.junit.jupiter.api;
    requires transitive org.junit.platform.engine;
    exports javax0.geci.jamal;
    exports javax0.geci.jamal.junit5;
    exports javax0.geci.jamal.util to geci.jamal.test;
    provides org.junit.platform.engine.TestEngine with Engine;
    provides javax0.jamal.api.Macro with
        Methods,
        Fields,
        Cap,
        Contains,
        ArgList,
        CallArgs,
        ClassList,
        IfNotVoid,
        IfVoid,
        Replace,
        Equals,
        Set
        ;
}