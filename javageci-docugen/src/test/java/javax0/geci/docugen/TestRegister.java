package javax0.geci.docugen;

import javax0.geci.tools.AbstractJavaGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestRegister {

    @Test
    @DisplayName("Registers all the generators ordered with increasing phases")
    void registerAllDocugen() {
        final var generators = Register.register().ordered().fileExtensions("md", "java", "adoc").allSnippetGenerators();
        int phase = 0;
        for (final var generatorBuilder : generators) {
            final var generator = generatorBuilder.build();
            Assertions.assertTrue(generator.activeIn(phase));
            Assertions.assertEquals(phase+1, generator.phases());
            phase++;
        }
    }

    @Test
    @DisplayName("Registers all the generators ordered with increasing phases through And().get()")
    void registerAllDocugenAnd() {
        final var generators = Register.register().ordered().fileExtensions("md", "java", "adoc").allSnippetGeneratorsAnd().get();
        int phase = 0;
        for (final var generatorBuilder : generators) {
            final var generator = generatorBuilder.build();
            Assertions.assertTrue(generator.activeIn(phase));
            Assertions.assertEquals(phase+1, generator.phases());
            phase++;
        }
    }

    @Test
    @DisplayName("Registers all the generators and numbering for once more")
    void registerAllDocugenAndAnother() {
        final var MNEMONIC = "postNumberer";
        final var generators = Register.register()
            .ordered()
            .fileExtensions("md", "java", "adoc").allSnippetGeneratorsAnd().add(SnippetNumberer.builder().mnemonic(MNEMONIC)).get();
        Assertions.assertEquals(Register.register().allSnippetGenerators().length+1,generators.length);
        int phase = 0;
        for (final var generatorBuilder : generators) {
            final var generator = generatorBuilder.build();
            Assertions.assertTrue(generator.activeIn(phase));
            Assertions.assertEquals(phase+1, generator.phases());
            phase++;
            if( phase == generators.length){
                Assertions.assertEquals(MNEMONIC,((AbstractSnippeter)generator).mnemonic());
            }
        }
    }

    @Test
    @DisplayName("Registers all the generators and numbering for once before all")
    void registerAllDocugenAfterAnother() {
        final var MNEMONIC = "postNumberer";
        final var generators = Register.register()
            .ordered()
            .fileExtensions("md", "java", "adoc").add(SnippetNumberer.builder().mnemonic(MNEMONIC)).allSnippetGeneratorsAnd().get();
        Assertions.assertEquals(Register.register().allSnippetGenerators().length+1,generators.length);
        int phase = 0;
        for (final var generatorBuilder : generators) {
            final var generator = generatorBuilder.build();
            Assertions.assertTrue(generator.activeIn(phase));
            Assertions.assertEquals(phase+1, generator.phases());
            if( phase == 0){
                Assertions.assertEquals(MNEMONIC,((AbstractSnippeter)generator).mnemonic());
            }
            phase++;
        }
    }

}
