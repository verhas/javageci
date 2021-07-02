package javax0.geci.jamal_test;

import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class PreprocessedFile {
    public int a;
    private Map myMap;

    public String mimosa(Integer a, Map myMap) {
        return null;
    }


    /*!jamal
    {%@define z=13%}int i = {%z%};
    {%@import res:javax0/geci/jamal_test/setters.jim%}\
//        {%#for ($modifiers,$type,$name,$arg) in ({%#methods (class=javax0.geci.jamal_test.PreprocessedFile selector=true argsep=: exsep=: format=$modifiers|$type|$name|$args)%})=
//         $modifiers $type $name $arg {%@comment
//
//%}        %}
    {%@define $class=javax0.geci.jamal_test.PreprocessedFile%}
    {%setters%}
     */
    int i = 13;
    //        
//         protected  Object clone          
//         protected  void finalize          
//         public  String mimosa java.lang.Integer:Map         
//         public  String toString          
//         public  boolean equals java.lang.Object         
//         public final  Class getClass          
//         public final  void notify          
//         public final  void notifyAll          
//         public final  void wait          
//         public final  void wait long         
//         public final  void wait long:int         
//         public  int hashCode          
//         public  void dummy          
//         public  void setA int         
//         public  void setI int         
//         public  void setMyMap Map         



    public void setA(int a){
        this.a = a;
        }

    public void setI(int i){
        this.i = i;
        }

    public void setMyMap(Map myMap){
        this.myMap = myMap;
        }


    //__END__
    public void dummy() {


        /*!jamal
        {%beginCode the generatedCode%}
        var j = {%z%};
        {%endCode%}
         */
        //<editor-fold desc="the generatedCode">
        var j = 13;
        //</editor-fold>
        //__END__

        /*!jamal
        {%beginCode the generatedCode%}
        {%@import variables.jam%}var k = {%s%};
        {%endCode%}
         */
        //<editor-fold desc="the generatedCode">
        var k = 666;
        //</editor-fold>
        //__END__
    }
}

