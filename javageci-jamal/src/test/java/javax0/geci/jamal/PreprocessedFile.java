package javax0.geci.jamal;

public class PreprocessedFile {
    public void dummy() {

        /*!jamal
        //<editor-fold desc="the generated code">
        {{@define z=13}}var i = {{z}};
        //{{#methods PreprocessedFileMacros|javax0.geci.jamal.PreprocessedFile|public}}
        //{{#PreprocessedFileMacros 0 signature}}
        //</editor-fold>
         */
        //<editor-fold desc="the generated code">
        var i = 13;
        //0
        //public void dummy()
        //</editor-fold>
        //__END__

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
