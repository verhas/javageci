package javax0.geci.tests.templated;

import javax0.geci.annotations.Geci;

@Geci("templatai")
public class NeedsTemplated {

    private static volatile int k = 1;

    //<editor-fold id="templatai">
    // this is preprocess
    // this is process for k
    // field.name k
    // field.genericString private static volatile int javax0.geci.tests.templated.NeedsTemplated.k
    // field.classSimpleName int
    // field.className int
    // field.classCanonicalName int
    // field.classPackage java.lang
    // field.classTypeName int
    // field.classGenericString int
    // this is template for fields
    // this is postprocess
    //</editor-fold>
}
