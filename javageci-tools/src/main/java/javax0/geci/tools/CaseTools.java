package javax0.geci.tools;

import java.util.function.Function;

public class CaseTools {

    public static String lcase(String s) {
        return xcase(s,String::toLowerCase);
    }

    public static String ucase(String s) {
        return xcase(s,String::toUpperCase);
    }

    private static String xcase(String s, Function<String,String> f) {
        return f.apply(s.substring(0, 1)) + (s.length() > 1 ? s.substring(1) : "");
    }

}
