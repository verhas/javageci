import javax0.geci.jamal.macros.Cap;
import javax0.geci.jamal.macros.Contains;
import javax0.geci.jamal.macros.Equals;
import javax0.geci.jamal.macros.Fields;
import javax0.geci.jamal.macros.IfNotVoid;
import javax0.geci.jamal.macros.IfVoid;
import javax0.geci.jamal.macros.Methods;
import javax0.geci.jamal.macros.Set;

import static javax0.geci.jamal.macros.ArgumentFormatters.ArgList;
import static javax0.geci.jamal.macros.ArgumentFormatters.CallArgs;
import static javax0.geci.jamal.macros.ArgumentFormatters.ClassList;

module geci.jamal {
    requires geci.engine;
    requires geci.tools;
    requires geci.api;
    requires jamal.engine;
    requires jamal.api;
    requires jamal.tools;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    exports javax0.geci.jamal;
    exports javax0.geci.jamal.util to geci.jamal.test;
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
        Equals,
        Set
        ;
}