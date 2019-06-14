package javax0.geci.tutorials.simple;

import javax0.geci.annotations.Geci;

@Geci("fluent definedBy='javax0.geci.tutorials.simple.TestSimpleGrammar::defineSimpleGrammar'")
//@Geci("fluent syntax='(singleWord | parameterisedWord | (word1 optionalWord?) | (word2 (wordChoiceA | wordChoiceB)) | word3+) end'")
public class SimpleGrammar {

    //<editor-fold id="fluent" desc="Java::Geci generated code">
    public static Uhab start(){
        return new Wrapper();
    }
    public static class Wrapper implements Ukeg,Abok,Efeh,Ujaj,Ogoj,Edak,Acuh,Aduf,Uhab,Ohug{
        private final javax0.geci.tutorials.simple.SimpleGrammar that;
        public Wrapper(){
            this.that = new javax0.geci.tutorials.simple.SimpleGrammar();
        }
        public Wrapper wordChoiceB(){
            that.wordChoiceB();
            return this;
        }
        public Wrapper optionalWord(){
            that.optionalWord();
            return this;
        }
        public Wrapper parameterisedWord(String arg1){
            that.parameterisedWord(arg1);
            return this;
        }
        public Wrapper wordChoiceA(){
            that.wordChoiceA();
            return this;
        }
        public Wrapper word1(){
            that.word1();
            return this;
        }
        public Wrapper word2(){
            that.word2();
            return this;
        }
        public void end(){
            that.end();
        }
        public Wrapper word3(){
            that.word3();
            return this;
        }
        public Wrapper singleWord(){
            that.singleWord();
            return this;
        }
    }
    public interface Aduf {
        void end();
    }
    public interface Ukeg {
        Aduf parameterisedWord(String arg1);
    }
    public interface Ohug {
        Aduf singleWord();
    }
    public interface Efeh extends Aduf {
        Aduf optionalWord();
    }
    public interface Acuh {
        Efeh word1();
    }
    public interface Ujaj{
        Aduf wordChoiceA();
        Aduf wordChoiceB();
    }
    public interface Ogoj {
        Ujaj word2();
    }
    public interface Edak extends Aduf {
        Edak word3();
    }
    public interface Abok {
        Edak word3();
    }
    public interface Uhab extends Ukeg,Abok,Ogoj,Acuh,Ohug{
    }

    //</editor-fold>

    public void singleWord() {
    }

    public void parameterisedWord(String parameter) {
    }

    public void word1() {
    }

    public void word2() {
    }

    public void word3() {
    }

    public void wordChoiceA() {
    }

    public void wordChoiceB() {
    }

    public void optionalWord() {
    }

    public void end() {
    }
}
