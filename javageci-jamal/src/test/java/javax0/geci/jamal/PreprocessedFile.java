package javax0.geci.jamal;

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
    {%@import res:javax0/geci/jamal/setters.jim%}\
//        {%#for ($modifiers,$type,$name,$arg) in ({%#methods
//                  {%class javax0.geci.jamal.PreprocessedFile%}
//                  {%selector=true%}
//                  {%argsep=:%}
//                  {%exsep=:%}
//                  {%format=$modifiers|$type|$name|$args%}%})=
//         $modifiers $type $name $arg {%@comment
//
//%}        %}
    {%class javax0.geci.jamal.PreprocessedFile%}
    {%setters%}
     */
    int i = 13;
    //        
//         private void setMyMap java.util.Map         
//         protected Object clone          
//         protected void finalize          
//         public String mimosa java.lang.Integer:java.util.Map         
//         public String toString          
//         public boolean equals java.lang.Object         
//         public final Class getClass          
//         public final void notify          
//         public final void notifyAll          
//         public final void wait          
//         public final void wait long         
//         public final void wait long:int         
//         public int hashCode          
//         public void dummy          
//         public void setA int         
//          void setI int         



    // PUBLIC
    public void setA(int a){ this.a = a; }

    // NOT PUBLIC
     void setI(int i){ this.i = i; }

    // NOT PUBLIC
    private void setMyMap(java.util.Map myMap){ this.myMap = myMap; }



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
