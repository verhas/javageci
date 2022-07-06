package javax0.geci.tools;

import javax0.geci.tools.reflection.Selector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TestSelector {

    @Test
    @DisplayName("The deprecated selector delegates he functions to the external implementation")
    void testCompatibility(TestInfo info){
        Selector.compile("package").match(info.getTestMethod().get());
    }
}
