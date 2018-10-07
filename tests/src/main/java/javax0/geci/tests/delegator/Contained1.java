package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

@Geci("accessor include='private,protected'")
public class Contained1 {

    public void callMe() {

    }

    private final String apple = "";
    @Geci("accessors only='setter'")
    private int birnen;

    int packge;

    @Geci("accessor access='protected'")
    protected boolean truth;
    @Geci("accessor exclude='yes'")
    protected int not_this;


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
