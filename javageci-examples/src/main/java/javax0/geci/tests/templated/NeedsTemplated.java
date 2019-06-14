package javax0.geci.tests.templated;

import javax0.geci.annotations.Geci;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Geci("templated selector='needs'")
public class NeedsTemplated {

    private static volatile int k = 1;
    @Geci("templated selector=''")
    private static volatile int j = 1;
    @Geci("templated selector='needed'")
    private static volatile List<Map<String, Set<Integer>>> h;

    //<editor-fold id="templated">
    // this is preprocess 112 for NeedsTemplated
    // the field private static volatile java.util.List<java.util.Map<java.lang.String, java.util.Set<java.lang.Integer>>> javax0.geci.tests.templated.NeedsTemplated.h; is needed

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
