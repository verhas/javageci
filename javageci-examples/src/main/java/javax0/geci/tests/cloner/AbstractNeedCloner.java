package javax0.geci.tests.cloner;

import javax0.geci.annotations.Geci;

public class AbstractNeedCloner {
    // snippet AbstractNeedCloner_fields
    public String inheritedField;
    @Geci("cloner filter='false'")
    public String inheritedExcludedField;
    // end snippet

    protected void mopy(AbstractNeedCloner it){
    }

}
