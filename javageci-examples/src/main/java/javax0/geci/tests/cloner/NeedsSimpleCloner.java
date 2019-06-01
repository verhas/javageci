package javax0.geci.tests.cloner;

import java.util.Map;
import java.util.Set;

public class NeedsSimpleCloner extends AbstractNeedCloner {
    private final int aInt = 10;
    protected int bINt = 55;

    Map<String, Set<Number>> map;

    //<editor-fold id="cloner" cloneWith="false" copyCallsSuper="true" superCopyMethod="mopy">
    @javax0.geci.annotations.Generated("cloner")
    public NeedsSimpleCloner copy() {
        final var it = new NeedsSimpleCloner();
        copy(it);
        return it;
    }
    protected void copy(NeedsSimpleCloner it) {
        super.mopy(it);

        it.bINt = bINt;
        it.inheritedExcludedField = inheritedExcludedField;
        it.inheritedField = inheritedField;
        it.map = map;
    }

    NeedsSimpleCloner withBINt(int bINt) {
        this.bINt = bINt;
        return this;
    }

    NeedsSimpleCloner withInheritedField(String inheritedField) {
        this.inheritedField = inheritedField;
        return this;
    }

    NeedsSimpleCloner withMap(java.util.Map map) {
        this.map = map;
        return this;
    }

    //</editor-fold>
}
