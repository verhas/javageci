package javax0.geci.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestSource {

    @Test
    @DisplayName("Test and document the different calls to the maven source directory structure helpers")
    void testMavenSourceBuildups() {
        /*
         * You can specify the main or test and java or resource directories. Simple use, no maven modules.
         */
        Assertions.assertEquals("./src/main/java", String.join(" ### ",
            Source.maven().mainSource().directories));
        Assertions.assertEquals("./src/test/java", String.join(" ### ",
            Source.maven().testSource().directories));
        Assertions.assertEquals("./src/main/resources", String.join(" ### ",
            Source.maven().mainResources().directories));
        Assertions.assertEquals("./src/test/resources", String.join(" ### ",
            Source.maven().testResources().directories));

        /*
         * You can specify the same as above, but also the maven module where the test code and the code needing code generation is.
         */
        Assertions.assertEquals("./module/src/main/java ### ../module/src/main/java ### ./src/main/java", String.join(" ### ",
            Source.maven().module("module").mainSource().directories));
        Assertions.assertEquals("./module/src/test/java ### ../module/src/test/java ### ./src/test/java", String.join(" ### ",
            Source.maven().module("module").testSource().directories));
        Assertions.assertEquals("./module/src/main/resources ### ../module/src/main/resources ### ./src/main/resources", String.join(" ### ",
            Source.maven().module("module").mainResources().directories));
        Assertions.assertEquals("./module/src/test/resources ### ../module/src/test/resources ### ./src/test/resources", String.join(" ### ",
            Source.maven().module("module").testResources().directories));

        /*
         * In the special case when the test calling the code generator and the code needing code generation are in
         * different maven modules we should specify where the root module is. This is usually {@code ..}.
         */
        Assertions.assertEquals("../module/src/main/java ### ./module/src/main/java", String.join(" ### ",
            Source.maven("..").module("module").mainSource().directories));
        Assertions.assertEquals("../module/src/test/java ### ./module/src/test/java", String.join(" ### ",
            Source.maven("..").module("module").testSource().directories));
        Assertions.assertEquals("../module/src/main/resources ### ./module/src/main/resources", String.join(" ### ",
            Source.maven("..").module("module").mainResources().directories));
        Assertions.assertEquals("../module/src/test/resources ### ./module/src/test/resources", String.join(" ### ",
            Source.maven("..").module("module").testResources().directories));
    }
}
