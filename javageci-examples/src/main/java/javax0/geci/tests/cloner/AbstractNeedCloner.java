package javax0.geci.tests.cloner;

import javax0.geci.annotations.Geci;

public class AbstractNeedCloner {
    public String inheritedField;
    @Geci("cloner filter='false'")
    public String inheritedExcludedField;

}
