package javax0.geci.tests.accessors;

import javax0.geci.annotations.Geci;

import java.util.Map;
import java.util.Set;

@Geci("caccessor filter='private | protected'")
public class NeedChainedAccessors<T> {

    public T t;

    @SuppressWarnings("EmptyMethod")
    public void callMe() {

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final String apple = "";
    @Geci("caccessors only='setter'")
    private int birnen;

    int packge;

    @Geci("accessor access='package' getter='isTrue'")
    protected boolean truth;
    @Geci("caccessor filter='false'")
    protected int not_this;

    public Map<String,Set<Map<Integer,Boolean>>> doNothingReally(int a, Map b, Set<Set> set){
        return null;
    }


    //<editor-fold id="caccessor" desc="setters">
    public String getApple() {
        return apple;
    }

    public NeedChainedAccessors<T> withBirnen(int birnen) {
        this.birnen = birnen;
        return this;
    }

    public int getBirnen() {
        return birnen;
    }

    public NeedChainedAccessors<T> withTruth(boolean truth) {
        this.truth = truth;
        return this;
    }

    public boolean getTruth() {
        return truth;
    }

    //</editor-fold>

}
