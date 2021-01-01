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

// {%@import res:geci.jim%}
//        {%#for ($modifiers,$type,$name,$arg) in ({%#methods
//                  {%class javax0.geci.jamal.PreprocessedFile%}
//                  {%selector=true%}
//                  {%argsep=:%}
//                  {%exsep=:%}
//                  {%format=$modifiers|$type|$name|$args%}%})=
//         $modifiers $type $name $arg {%@comment
//
//%}        %}
    {%#for ($modifiers,$name,$type) in ({%#fields
                            {%class javax0.geci.jamal.PreprocessedFile%}
                            {%format=$modifiers|$name|$type%}%})=
    $modifiers void set$name($type $name){ this.$name = $name; }
    %}

     */
    int i = 13;

// 
//        
//         private void setmyMap java.util.Map         
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
//         public void seta int         
//          void seti int         

    public void seta(int a){ this.a = a; }

     void seti(int i){ this.i = i; }

    private void setmyMap(java.util.Map myMap){ this.myMap = myMap; }



    //__END__
    public void dummy() {


        /*!jamal
        //<editor-fold desc="the generated code">
        var j = {%z%};
        //</editor-fold>
         */
        //<editor-fold desc="the generated code">
        var j = 13;
        //</editor-fold>

        //__END__

        /*!jamal
        //<editor-fold desc="the generated code">
        {%@import variables.jam%}var k = {%s%};
        //</editor-fold>
         */
        //<editor-fold desc="the generated code">
        var k = 666;
        //</editor-fold>

        //__END__
    }
}
