package javax0.geci.tests.cloner;

public class NeedCloner extends AbstractNeedCloner {
    private final int aInt = 10;
    protected int bINt = 55;

    //<editor-fold id="cloner">
    @javax0.geci.annotations.Generated("cloner")
    public NeedCloner clone() {
        final var it = new NeedCloner();

        it.bINt = bINt;
        it.inheritedExcludedField = inheritedExcludedField;
        it.inheritedField = inheritedField;
        return it;
    }

    NeedCloner withBINt(int bINt) {
        final var it = clone();
        it.bINt = bINt;
        return it;
    }

    NeedCloner withInheritedField(String inheritedField) {
        final var it = clone();
        it.inheritedField = inheritedField;
        return it;
    }

    //</editor-fold>
}
