package javax0.geci.jamal;

import java.util.Map;

public class PreprocessedFile {
    public int a;
    private Map myMap;

    public String mimosa(Integer a, Map myMap) {
        return null;
    }


    /*!jamal
    {{@define z=13}}int i = {{z}};

//        {{#for listedMethod in ({{#methods javax0.geci.jamal.PreprocessedFile/public}})=
//         listedMethod {{@comment
//}}        }}
    {{#eval {{#for listedFields in ({{#fields javax0.geci.jamal.PreprocessedFile/true}})=
       {{@ident {{#modifiers listedFields}} void set{{#name listedFields}}({{#type listedFields}} {{#name listedFields}}){
            this.{{#name listedFields}} = {{#name listedFields}};
       } }}

    }}}}

     */
    int i = 13;

//        
//         javax0.geci.jamal.PreprocessedFile|mimosa|java.lang.Integer|java.util.Map         
//         java.lang.Object|toString|         
//         java.lang.Object|equals|java.lang.Object         
//         java.lang.Object|getClass|         
//         java.lang.Object|notify|         
//         java.lang.Object|notifyAll|         
//         java.lang.Object|wait|         
//         java.lang.Object|wait|long         
//         java.lang.Object|wait|long|int         
//         java.lang.Object|hashCode|         
//         javax0.geci.jamal.PreprocessedFile|dummy|         
//         javax0.geci.jamal.PreprocessedFile|seta|int         
    public void seta(int a){
            this.a = a;
       } 


        void seti(int i){
            this.i = i;
       } 


       private void setmyMap(java.util.Map myMap){
            this.myMap = myMap;
       } 


    //__END__
    public void dummy() {


        /*!jamal
        //<editor-fold desc="the generated code">
        var j = {{z}};
        //</editor-fold>
         */
        //<editor-fold desc="the generated code">
        var j = 13;
        //</editor-fold>
        //__END__

        /*!jamal
        //<editor-fold desc="the generated code">
        {{@import variables.jam}}var k = {{s}};
        //</editor-fold>
         */
        //<editor-fold desc="the generated code">
        var k = 666;
        //</editor-fold>
        //__END__
    }
}
