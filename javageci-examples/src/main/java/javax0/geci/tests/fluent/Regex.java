package javax0.geci.tests.fluent;

import javax0.geci.annotations.Geci;

import java.util.regex.Pattern;

@Geci("fluent definedBy='javax0.geci.tests.fluent.TestFluent::regex'")
public class Regex {

    private final StringBuilder sb = new StringBuilder();

    public Regex copy(){
        var clone = new Regex();
        clone.sb.append(sb.toString());
        return clone;
    }

    public Pattern get(){
        return Pattern.compile(sb.toString());
    }

    public void terminal(String s){
        sb.append(s);
    }

    public void set(String s){
        sb.append("[").append(s).append("]");
    }

    public void optional(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")?");
    }

    public void zeroOrMore(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")*");
    }

    public void oneOrMore(Regex q){
        sb.append("(?:").append(q.sb.toString()).append(")+");
    }

    public void more(Regex q, int min, int max){
        sb.append("(?:").append(q.sb.toString()).append("){").append(min).append(",").append(max).append("}");
    }

    //<editor-fold id="fluent" desc="generated fluent code">
    public static Efeh pattern(){
        return new Wrapper();
    }
    public interface WrapperInterface{
    }
    public static class Wrapper implements Ukeg,Efeh,Aduf,Ohug{
        private final javax0.geci.tests.fluent.Regex that;
        public Wrapper(javax0.geci.tests.fluent.Regex that){
            this.that = that;
        }
        public Wrapper(){
            this.that = new javax0.geci.tests.fluent.Regex();
        }
        public Wrapper more(WrapperInterface arg1, int arg2, int arg3){
            var next = new Wrapper(that.copy());
            next.that.more(((Wrapper)arg1).that,arg2,arg3);
            return next;
        }
        public Wrapper set(String arg1){
            var next = new Wrapper(that.copy());
            next.that.set(arg1);
            return next;
        }
        public Wrapper terminal(String arg1){
            var next = new Wrapper(that.copy());
            next.that.terminal(arg1);
            return next;
        }
        public Wrapper oneOrMore(WrapperInterface arg1){
            var next = new Wrapper(that.copy());
            next.that.oneOrMore(((Wrapper)arg1).that);
            return next;
        }
        public Wrapper zeroOrMore(WrapperInterface arg1){
            var next = new Wrapper(that.copy());
            next.that.zeroOrMore(((Wrapper)arg1).that);
            return next;
        }
        public Wrapper optional(WrapperInterface arg1){
            var next = new Wrapper(that.copy());
            next.that.optional(((Wrapper)arg1).that);
            return next;
        }
        public java.util.regex.Pattern get(){
            return that.get();
        }
    }
    public interface Aduf extends WrapperInterface {
        java.util.regex.Pattern get();
    }
    public interface Ohug{
        Ukeg more(WrapperInterface arg1, int arg2, int arg3);
        Ukeg oneOrMore(WrapperInterface arg1);
        Ukeg optional(WrapperInterface arg1);
        Ukeg set(String arg1);
        Ukeg terminal(String arg1);
        Ukeg zeroOrMore(WrapperInterface arg1);
    }
    public interface Ukeg extends WrapperInterface,Aduf,Ohug {}
    public interface Efeh{
        Ukeg more(WrapperInterface arg1, int arg2, int arg3);
        Ukeg oneOrMore(WrapperInterface arg1);
        Ukeg optional(WrapperInterface arg1);
        Ukeg set(String arg1);
        Ukeg terminal(String arg1);
        Ukeg zeroOrMore(WrapperInterface arg1);
    }

    //</editor-fold>

}
