package javax0.geci.jamal;

public class PreprocessedFile {
    public void dummy(){

    /*!jamal
    {{@define z=13}}var i = {{z}};
     */
    var i = 13;
    //__END__
    }
}
