module geci.examples {
    requires geci.engine;
    requires geci.tools;
    requires geci.api;
    requires jamal.engine;
    requires jamal.api;
    requires jamal.tools;
    provides javax0.jamal.api.Macro with
        javax0.geci.jamal.reflection.Methods,
        javax0.geci.jamal.reflection.Fields,
        javax0.geci.jamal.reflection.Modifiers,
        javax0.geci.jamal.reflection.Name,
        javax0.geci.jamal.reflection.Type
        ;
}