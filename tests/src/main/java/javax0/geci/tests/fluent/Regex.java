package javax0.geci.tests.fluent;

import javax0.geci.annotations.Geci;

import java.util.regex.Pattern;

@Geci("fluent definedBy='javax0.geci.tests.fluent.TestFluent::regex'\")")
public class Regex {

    private final StringBuilder sb = new StringBuilder();

    Pattern get(){
        return Pattern.compile(sb.toString());
    }

    void terminal(String s){
        sb.append(s);
    }

    void set(String s){
        sb.append("[").append(s).append("]");
    }

    void optional(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")?");
    }

    void zeroOrMore(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")*");
    }

    void oneOrMore(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")+");
    }

    void more(Regex q, int min, int max){
        sb.append("(?:").append(q.sb.toString()).append("){").append(min).append(",").append(max).append("}");
    }

    //<editor-fold id="fluent" desc="generated fluent code">
    //</editor-fold>

}
