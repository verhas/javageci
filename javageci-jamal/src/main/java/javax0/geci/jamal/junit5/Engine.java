package javax0.geci.jamal.junit5;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import java.util.Optional;

public class Engine implements TestEngine {
    @Override
    public String getId() {
        return "Junit5::Geci";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {
        System.out.println("Junit5::Geci discover()");
        return new AbstractTestDescriptor(UniqueId.root(getId(),"wuff"),"Junit5::Jamal::Geci") {
            @Override
            public Type getType() {
                return Type.CONTAINER;
            }
        };
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        System.out.println("Junit5::Geci execute()");
    }

    @Override
    public Optional<String> getGroupId() {
        return Optional.of("com.javax0.geci");
    }

    @Override
    public Optional<String> getArtifactId() {
        return Optional.of("javageci-jamal");
    }

    @Override
    public Optional<String> getVersion() {
        return Optional.of("1.6.3");
    }
}
