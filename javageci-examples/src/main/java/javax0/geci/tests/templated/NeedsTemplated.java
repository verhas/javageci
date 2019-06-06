package javax0.geci.tests.templated;

import javax0.geci.annotations.Geci;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Geci("templated selector='huhh'")
public class NeedsTemplated {

    private static volatile int k = 1;
    @Geci("templated selector='needs'")
    private static volatile int j = 1;
    @Geci("templated selector='needed'")
    private static volatile List<Map<String, Set<Integer>>> h;

    //<editor-fold id="templated">
    // the field private static volatile java.util.List<java.util.Map<java.lang.String, java.util.Set<java.lang.Integer>>> javax0.geci.tests.templated.NeedsTemplated.h; is needed
    // this is process for j
    // field.name j
    // field.genericString private static volatile int javax0.geci.tests.templated.NeedsTemplated.j
    // field.classSimpleName int
    // field.className int
    // field.classCanonicalName int
    // field.classPackage java.lang
    // field.classTypeName int
    // field.classGenericString int
    //</editor-fold>
}
