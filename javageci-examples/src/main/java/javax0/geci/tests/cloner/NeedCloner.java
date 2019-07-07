package javax0.geci.tests.cloner;

public class NeedCloner extends AbstractNeedCloner {
    // snippet NeedCloner_fields
    private final int aInt = 10;
    protected int bINt = 55;
    // end snippet

    // snippet NeedCloner_generated_code
    //<editor-fold id="cloner">
    @javax0.geci.annotations.Generated("cloner")
    public NeedCloner copy() {
        final var it = new NeedCloner();
        copy(it);
        return it;
    }
    protected void copy(NeedCloner it) {

        it.bINt = bINt;
        it.inheritedExcludedField = inheritedExcludedField;
        it.inheritedField = inheritedField;
    }

    NeedCloner withBINt(int bINt) {
        final var it = copy();
        it.bINt = bINt;
        return it;
    }

    NeedCloner withInheritedField(String inheritedField) {
        final var it = copy();
        it.inheritedField = inheritedField;
        return it;
    }

    //</editor-fold>
    // end snippet
}
