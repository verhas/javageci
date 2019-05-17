package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestPhasedExecution {
    private final StringBuilder testString = new StringBuilder();

    @Test
    @DisplayName("Test that generators are invoked only for the phases that needed")
    void testPhasedExecution() throws Exception {
        testString.setLength(0);
        new Geci().only("TestPhasedExecution.java")
                .register(new A(), new B()).generate();
        Assertions.assertEquals(
                "Phase 0 generator 'A'\n" +
                        "Phase 1 generator 'B'\n" +
                        "Phase 2 generator 'A'\n", testString.toString()
        );
    }

    //<editor-fold id="touchit">
    //</editor-fold>
    class A implements Generator {
        int actualPhase;

        @Override
        public void process(javax0.geci.api.Source source) {
            try {
                source.init("touchit");
            } catch (IOException e) {
                throw new GeciException("Cannot touch it!");
            }
            testString.append("Phase ")
                    .append(actualPhase)
                    .append(" generator '")
                    .append(this.getClass().getSimpleName())
                    .append("'\n");
        }

        @Override
        public boolean activeIn(int phase) {
            actualPhase = phase;
            return phase == 0 || phase == 2;
        }

        @Override
        public int phases() {
            return 3;
        }
    }

    class B implements Generator {
        int actualPhase;

        @Override
        public void process(javax0.geci.api.Source source) {
            testString.append("Phase ")
                    .append(actualPhase)
                    .append(" generator '")
                    .append(this.getClass().getSimpleName())
                    .append("'\n");
        }

        @Override
        public boolean activeIn(int phase) {
            actualPhase = phase;
            return phase == 1;
        }

        @Override
        public int phases() {
            return 2;
        }
    }
}
