import javax0.geci.jamal.reflection.Cap;
import javax0.geci.jamal.reflection.Contains;
import javax0.geci.jamal.reflection.Equals;
import javax0.geci.jamal.reflection.Fields;
import javax0.geci.jamal.reflection.IfNotVoid;
import javax0.geci.jamal.reflection.IfVoid;
import javax0.geci.jamal.reflection.Methods;
import javax0.geci.jamal.reflection.Replace;

import static javax0.geci.jamal.reflection.ArgumentFormatters.ArgList;
import static javax0.geci.jamal.reflection.ArgumentFormatters.CallArgs;
import static javax0.geci.jamal.reflection.ArgumentFormatters.ClassList;

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
        Equals
        ;
}