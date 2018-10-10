package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

import java.util.Map;
import java.util.Set;

@Geci("accessor include='private,protected'")
public class Contained1 {

    @SuppressWarnings("EmptyMethod")
    public void callMe() {

    }

    @SuppressWarnings("FieldCanBeLocal")
    private final String apple = "";
    @Geci("accessors only='setter'")
    private int birnen;

    int packge;

    @Geci("accessor access='protected'")
    protected boolean truth;
    @Geci("accessor exclude='yes'")
    protected int not_this;

    public Map<String,Set<Map<Integer,Boolean>>> doNothingReally(int a, Map b, Set<Set> set){
        return null;
    }


    //<editor-fold id="accessor" desc="setters">
    public String getApple(){
        return apple;
    }

    public void setBirnen(int birnen){
        this.birnen = birnen;
    }

    public int getBirnen(){
        return birnen;
    }

    protected void setTruth(boolean truth){
        this.truth = truth;
    }

    protected boolean getTruth(){
        return truth;
    }

    //</editor-fold>

}
