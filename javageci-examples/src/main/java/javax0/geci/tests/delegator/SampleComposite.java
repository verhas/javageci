package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

@Geci("delegator filter='name ~ /^a/ | simpleName ~ /^B/'")
public class SampleComposite<K, V> {

    @Geci("delegator methods='!protected & !private'")
    private AClass a;
    @Geci("delegator methods='!private'")
    private BClass b;
    @Geci("delegator filter='true'")
    private CClass c;


    // <editor-fold id="delegator" desc="delegated methods to contained1">
    @javax0.geci.annotations.Generated("delegator")
    public long anaesthesia(long arg1) throws InterruptedException {
        return a.anaesthesia(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void abrakaDabra(String arg1, int arg2) {
        a.abrakaDabra(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    void blabla(String arg1) {
        b.blabla(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void colossal() {
        c.colossal();
    }

    // </editor-fold>

}
