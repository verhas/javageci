package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

public class SampleComposite {
    @Geci("delegate id='contained1'")
    Contained1 contained1;

    // <editor-fold id="contained1" desc="delegated methods to contained1">
    public String getApple(){
        return contained1.getApple();
    }
    public int getBirnen(){
        return contained1.getBirnen();
    }
    public void setBirnen(){
        contained1.setBirnen(1);
    }
    public void callMe(){
        contained1.callMe();
    }
    // </editor-fold>

}
