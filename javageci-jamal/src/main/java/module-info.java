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
    requires geci.engine;
    requires geci.tools;
    requires geci.api;
    requires jamal.engine;
    requires jamal.api;
    requires jamal.tools;
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