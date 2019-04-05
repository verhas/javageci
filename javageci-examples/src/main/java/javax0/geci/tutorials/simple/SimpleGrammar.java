package javax0.geci.tutorials.simple;

import javax0.geci.annotations.Geci;

@Geci("fluent definedBy='javax0.geci.tutorials.simple.TestSimpleGrammar::defineSimpleGrammar'")
//@Geci("fluent syntax='(singleWord | parameterisedWord | (word1 optionalWord?) | (word2 (wordChoiceA | wordChoiceB)) | word3+) end'")
public class SimpleGrammar {

    //<editor-fold id="fluent" desc="Java::Geci generated code">
    public static If9 start(){
        return new Wrapper();
    }
    public static class Wrapper implements If0,If2,If1,If4,If3,If6,If5,If8,If7,If9{
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
    public interface If0 {
        void end();
    }
    public interface If1 {
        If0 parameterisedWord(String arg1);
    }
    public interface If2 {
        If0 singleWord();
    }
    public interface If3 extends If0 {
        If0 optionalWord();
    }
    public interface If4 {
        If3 word1();
    }
    public interface If5{
        If0 wordChoiceA();
        If0 wordChoiceB();
    }
    public interface If6 {
        If5 word2();
    }
    public interface If7 extends If0 {
        If7 word3();
    }
    public interface If8 {
        If7 word3();
    }
    public interface If9 extends If2,If1,If4,If6,If8{
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
