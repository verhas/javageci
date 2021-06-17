package javax0.geci.jamal.junit5;

import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import javax0.geci.jamal.JamalGenerator;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static javax0.geci.api.Source.maven;

public class Engine implements TestEngine {
    @Override
    public String getId() {
        return "Junit5::Geci";
    }

    private static final Logger LOG = LoggerFactory.getLogger();

    private TestDescriptor testDescriptor;

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {
        if (testDescriptor != null) {
            LOG.info("discovery invocation repeated");
            return testDescriptor;
        }
        LOG.info("discovery invoked");
        testDescriptor = new GeciJamalTestDescriptor(UniqueId.root(getId(), "Geci::Jamal"), "Geci::Jamal code generation execution", new GeciTestSource());

        return testDescriptor;
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        final var cp = executionRequest.getConfigurationParameters();
        final var td = executionRequest.getRootTestDescriptor();
        final var listener = executionRequest.getEngineExecutionListener();
        listener.executionStarted(td);
        javax0.geci.api.Geci geci = new Geci();
        for (final var s : getNamedSourceSet(cp)) {
            geci = geci.source(s);
        }

        try {
            LOG.info("GECI Jamal executing code generator for all the files in the source set");
            if (geci.register(new JamalGenerator())
                .generate()) {
                listener.executionFinished(td, TestExecutionResult.failed(new AssertionFailedError(((Geci) geci).failed(), "", "")));
            } else {
                listener.executionFinished(td, TestExecutionResult.successful());
            }
        } catch (Exception e) {
            LOG.info("GECI Jamal There was an exception executing the code generation.");
            listener.executionFinished(td, TestExecutionResult.aborted(e));
        }
        LOG.info("Junit5::Geci execute()");
    }

    private List<Source.NamedSourceSet> getNamedSourceSet(ConfigurationParameters cp) {
        final var sets = new ArrayList<Source.NamedSourceSet>();
        final var mavenSourceSet = getConfiguredMavenSourceSet(cp);
        if (mavenSourceSet != null) {
            sets.add(mavenSourceSet);
        }
        final var setsOpt = cp.get("geci.sourceSets");
        if (setsOpt.isPresent()) {
            LOG.info("GECI Jamal source sets defined are: ", setsOpt.get());
            for (final var setName : setsOpt.get().split(",")) {
                final var setOpt = cp.get(setName);
                if (setOpt.isEmpty()) {
                    throw new TestAbortedException(format("Set named '%s' is not configured in the file '%s'",
                        setName, ConfigurationParameters.CONFIG_FILE_NAME));
                }
                sets.add(new Source.NamedSourceSet(Source.Set.set(setName), setOpt.get().split(",")));
            }
        }

        if (sets.size() == 0) {
            LOG.info("GECI Jamal using default source set");
            sets.add(maven().mainSource());
        }
        return sets;
    }

    private Source.NamedSourceSet getConfiguredMavenSourceSet(ConfigurationParameters cp) {
        var maven = maven();
        final var moduleOpt = cp.get("geci.maven.module");
        if (moduleOpt.isPresent()) {
            LOG.info("GECI Jamal processing source in the maven module %s", moduleOpt.get());
            maven = maven.module(moduleOpt.get());
        }
        final var sourceOpt = cp.get("geci.maven.source");
        final Source.NamedSourceSet mavenSourceSet;
        if (sourceOpt.isPresent()) {
            switch (sourceOpt.get()) {
                case "testSource":
                    mavenSourceSet = maven.testSource();
                    break;
                case "mainResources":
                    mavenSourceSet = maven.mainResources();
                    break;
                case "testResources":
                    mavenSourceSet = maven.testResources();
                    break;
                default:
                    mavenSourceSet = maven.mainSource();
                    break;
            }
        } else {
            mavenSourceSet = maven.mainSource();
        }
        if (moduleOpt.isPresent() || sourceOpt.isPresent()) {
            LOG.info("GECI Jamal maven %s=(%s)", mavenSourceSet.set.toString(), String.join(",", mavenSourceSet.directories));
            return mavenSourceSet;
        } else {
            return null;
        }
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
