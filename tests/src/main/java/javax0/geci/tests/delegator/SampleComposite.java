package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

@Geci("delegator")
public class SampleComposite {
    @Geci("delegator id='contained1'")
    Contained1 contained1;

    // <editor-fold id="contained1" desc="delegated methods to contained1">
    public String getApple() {
        return contained1.getApple();
    }

    public int getBirnen() {
        return contained1.getBirnen();
    }

    public java.util.Map<String,java.util.Set<java.util.Map<Integer,Boolean>>> doNothingReally(int arg1, java.util.Map arg2, java.util.Set<java.util.Set> arg3) {
        return contained1.doNothingReally(arg1,arg2,arg3);
    }

    public void callMe() {
        contained1.callMe();
    }

    public void setBirnen(int arg1) {
        contained1.setBirnen(arg1);
    }

    // </editor-fold>

}
