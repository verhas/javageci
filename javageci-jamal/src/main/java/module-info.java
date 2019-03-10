module geci.examples {
    requires geci.engine;
    requires geci.tools;
    requires geci.api;
    requires jamal.engine;
    requires jamal.api;
    requires jamal.tools;
    provides javax0.jamal.api.Macro with
        javax0.geci.jamal.Reflection.Methods;
}