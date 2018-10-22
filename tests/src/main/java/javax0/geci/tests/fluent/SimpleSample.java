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
    public static If11 sample(){
        return new Wrapper();
    }
    public static class Wrapper implements If0,If2,If1,If4,If3,If6,If5,If8,If7,If9,If10,If11{
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
    interface If0{
        String get();
        String got();
    }
    interface If2 {
        If1 b(String arg1);
    }
    interface If3 {
        If2 a(String arg1);
    }
    interface If4 {
        If1 d(String arg1);
    }
    interface If5 {
        If4 c(String arg1);
    }
    interface If6 extends If3,If5{
    }
    interface If1 extends If0,If6 {}
    interface If7 {
        If1 b(String arg1);
    }
    interface If8 {
        If7 a(String arg1);
    }
    interface If9 {
        If1 d(String arg1);
    }
    interface If10 {
        If9 c(String arg1);
    }
    interface If11 extends If8,If10{
    }
    //</editor-fold>

}
