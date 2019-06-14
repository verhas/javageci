package javax0.geci.tests.fluent;

import javax0.geci.annotations.Geci;

@Geci("fluent definedBy='javax0.geci.tests.fluent.TestFluent::definition'")
public class SimpleSample {

    public SimpleSample copy(){
        return  this;
    }

    private final StringBuilder sb = new StringBuilder();

    public void a(String s){
        sb.append("A(").append(s).append(")");
    }
    public void b(String s){
        sb.append("B(").append(s).append(")");
    }
    public void c(String s){
        sb.append("C(").append(s).append(")");
    }
    public void d(String s){
        sb.append("D(").append(s).append(")");
    }

    public String get(){
        return sb.toString();
    }
    public String got(){
        return sb.toString();
    }

    //<editor-fold id="fluent" desc="generated fluent code">
    public static Ecac sample(){
        return new Wrapper();
    }
    public static class Wrapper implements Ecac,Abok,Efeh,Edak,Acuh,Aduf,Ohug,Ofob,Ukeg,Ujaj,Ogoj,Uhab{
        private final javax0.geci.tests.fluent.SimpleSample that;
        public Wrapper(javax0.geci.tests.fluent.SimpleSample that){
            this.that = that;
        }
        public Wrapper(){
            this.that = new javax0.geci.tests.fluent.SimpleSample();
        }
        public Wrapper b(String arg1){
            var next = new Wrapper(that.copy());
            next.that.b(arg1);
            return next;
        }
        public String got(){
            return that.got();
        }
        public Wrapper c(String arg1){
            var next = new Wrapper(that.copy());
            next.that.c(arg1);
            return next;
        }
        public String get(){
            return that.get();
        }
        public Wrapper a(String arg1){
            var next = new Wrapper(that.copy());
            next.that.a(arg1);
            return next;
        }
        public Wrapper d(String arg1){
            var next = new Wrapper(that.copy());
            next.that.d(arg1);
            return next;
        }
    }
    public interface Aduf{
        String get();
        String got();
    }
    public interface Ohug {
        Ukeg b(String arg1);
    }
    public interface Efeh {
        Ohug a(String arg1);
    }
    public interface Acuh {
        Ukeg d(String arg1);
    }
    public interface Ujaj {
        Acuh c(String arg1);
    }
    public interface Ogoj extends Efeh,Ujaj{
    }
    public interface Ukeg extends Ogoj,Aduf {}
    public interface Edak {
        Ukeg b(String arg1);
    }
    public interface Abok {
        Edak a(String arg1);
    }
    public interface Uhab {
        Ukeg d(String arg1);
    }
    public interface Ofob {
        Uhab c(String arg1);
    }
    public interface Ecac extends Abok,Ofob{
    }

    //</editor-fold>

}
