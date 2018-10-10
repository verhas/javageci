package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

@Geci("delegator")
public class SampleComposite {
    @Geci("delegator id='contained1'")
    Contained1 contained1;

    // <editor-fold id="contained1" desc="delegated methods to contained1">
    public String getApple(){
        return contained1.getApple();
    }
    public void setBirnen(int i1){
        contained1.setBirnen( i1);
    }
    public int getBirnen(){
        return contained1.getBirnen();
    }
    public void callMe(){
        contained1.callMe();
    }
    // </editor-fold>

}
