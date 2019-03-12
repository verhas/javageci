module geci.examples {
    requires geci.engine;
    requires geci.tools;
    requires geci.api;
    requires jamal.engine;
    requires jamal.api;
    requires jamal.tools;
    provides javax0.jamal.api.Macro with
        javax0.geci.jamal.Reflection.Methods,
        javax0.geci.jamal.Reflection.Fields,
        javax0.geci.jamal.Reflection.Modifiers,
        javax0.geci.jamal.Reflection.Name,
        javax0.geci.jamal.Reflection.Type
        ;
}