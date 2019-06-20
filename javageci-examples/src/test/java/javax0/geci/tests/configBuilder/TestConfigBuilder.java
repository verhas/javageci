package javax0.geci.tests.configBuilder;

import javax0.geci.config.ConfigBuilder;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestConfigBuilder {

    @Test
    void buildGenerators() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(
                        "./javageci-core/src/main/java/",
                        "../javageci-core/src/main/java/")
                        .register(ConfigBuilder.builder().build())
                        .generate(),
                geci.failed()
        );
    }
}
