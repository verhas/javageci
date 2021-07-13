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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A JUnit5 test engine that will start the Java::Geci Jamal generator for the source and resources directories on the
 * project overwriting the source files. This engine is automatically invoked by the JUnit 5 framework if the module
 * containing this class is on the classpath or it is on the modulepath.
 * <p>
 * This test engine makes the use of the Java::Geci Jamal generator as simple as adding a dependency to the project.
 * <p>
 * Most frameworks will invoke this engine if there is somethign testable in the project. It means that you should have
 * at least one unit test.
 * <p>
 * A side effect, having this engine configured into your project that it will always run generating the code, or at
 * least checking that the code is properly generated and does not need regeneration. When this engine is configured
 * you cannot just start one test from your IDE, that will also run this generator.
 * <p>
 * Note that the code generation "test" will report a failure if code was generated. This is to signal that your source
 * code was changed, and you need to recompile the code. When this engine reports failure the solution is usually to run
 * it again.
 * <p>
 * In some rare cases, when a generated code depends on other code, which is also generated, it may happen that you need
 * to run the compilation, and the tests a few times. It is possible to create circular dependency with contradicting
 * generated code. In that case the code generation will never succeed. This situation is not likely and in practice it
 * should not happen. If it does your code generation structures and dependencies are severely flowed.
 */
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
        for (final var s : getNamedSourceSets(cp)) {
            geci = geci.source(s);
        }
        for (final var s : getIgnorePatterns(cp)) {
            geci = geci.ignore(s);
        }
        for (final var s : getOnlyPatterns(cp)) {
            geci = geci.only(s);
        }
        final var ignoreBinary = cp.getBoolean("geci.ignoreBinary");
        if (ignoreBinary.isPresent() && ignoreBinary.get()) {
            geci.ignoreBinary();
        }
        final var diffOutput = cp.get("geci.diffOutput");
        if( diffOutput.isPresent()){
            geci = geci.diffOutput(diffOutput.get());
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
            listener.executionFinished(td, TestExecutionResult.failed(e));
        }
        LOG.info("Junit5::Geci execute()");
    }

    /**
     * Get the patterns from the configuration that should be ignored.
     * <p>
     * These patterns will be passed as strings to the {@link Geci#ignore(String...)} method.
     * <p>
     * Configuration usually looks like:
     * <pre>{@code
     * # ignore all the text files
     * geci.ignore=.*\.txt
     * # additional patterns can be defined with numbering the keys
     * geci.ignore.0=.*\.pdf
     * geci.ignore.1=.*\.zip
     * geci.ignore.2=.*\.zap
     * ...
     * }</pre>
     * <p>
     * The configuration key is {@code geci.ignore} and/or {@code geci.ignore.N} where {@code N} should be a non-negative integer number.
     * The number {@code N} should not have leading zeroes, spaces or + sign at the start.
     * All {@code geci.ignore.N} values will be processed where {@code N} is
     * less than 100. If there are more than 99 items then the processing will stop when it does not find a key for a
     * given {@code N}. This way, for example
     *
     * <pre>{@code
     * geci.ignore=this will always be processed
     * geci.ignore.0=can start with zero, or 1 or anything below 100
     * geci.ignore.1=just usual to number continuously
     * geci.ignore.7=missing 2,3,...,6 is not a problem
     * ...
     * geci.ignore.99=it is not a problem that numbers are missing from the configuration below 100
     * geci.ignore.100=still works, because there was 99
     * geci.ignore.101=still works, because there was 100
     * geci.ignore.103=this is not used because processing stopped when there was no 102
     * }</pre>
     *
     * @param cp the configuration
     * @return the list of the patters
     */
    private List<String> getIgnorePatterns(ConfigurationParameters cp) {
        return getPatterns(cp, "ignore");
    }

    /**
     * Get the patterns from the configuration that should be included only.
     * <p>
     * These patterns will be passed as strings to the {@link Geci#only(String...)} method.
     * If no value is configured the {@link Geci#only(String...)} will not be invoked.
     * <p>
     * Configuration usually looks like:
     * <pre>{@code
     * # include only the '.java' files
     * geci.only=.*\.java$
     * # additional patterns can be defined with numbering the keys
     * geci.only.0=.*\.kotlin
     * geci.only.1=.*\.groovy
     * geci.only.2=.*\.rb
     * ...
     * }</pre>
     * The numbers should follow the same rules and are processed the same way as in case of the {@code geci.ignore}
     * option. For more information see the documentation of the method
     * {@link #getIgnorePatterns(ConfigurationParameters) getIgnorePatterns()}.
     *
     * @param cp the configuration
     * @return the list of the patters
     */
    private List<String> getOnlyPatterns(ConfigurationParameters cp) {
        return getPatterns(cp, "only");
    }

    private List<String> getPatterns(ConfigurationParameters cp, String keyword) {
        final var ignores = new ArrayList<String>();
        var ignore = cp.get("geci." + keyword);
        if (ignore.isPresent()) {
            ignores.add(ignore.get());
        }
        for (int i = 0; true; i++) {
            ignore = cp.get("geci." + keyword + "." + i);
            if (!ignore.isPresent() && i > 100) {
                break;
            }
            if (ignore.isPresent()) {
                ignores.add(ignore.get());
            }
        }
        return ignores;
    }

    /**
     * Get the directory sets as named sets, if they are configured in the 'junit-platform.properties' file.
     * If nothing is configured then the maven directories, {@code src/main/java}, {@code src/test/java}, {@code
     * src/main/resources} and {@code src/test/resources} will be used.
     * <p>
     * To configure the source directories the key {@code geci.sourceSets} should be the comma separated list of the
     * source set names. Each listed source set name should be present in the file as a key and should have the comma
     * separated list of the directories that belong to that set.
     *
     * @param cp the configuration read by the framework
     * @return the list of the source sets on which the Jamal generator should work on
     */
    private List<Source.NamedSourceSet> getNamedSourceSets(ConfigurationParameters cp) {
        final var sets = new ArrayList<Source.NamedSourceSet>();
        final var setsOpt = cp.get("geci.sourceSets");
        if (setsOpt.isPresent()) {
            LOG.info("GECI Jamal source sets defined are: ", setsOpt.get());
            for (final var setName : Arrays.stream(setsOpt.get().split(",")).map(String::trim).collect(Collectors.toList())) {
                final var setOpt = cp.get(setName);
                if (setOpt.isEmpty()) {
                    throw new TestAbortedException(format("Set named '%s' is not configured in the file '%s'",
                        setName, ConfigurationParameters.CONFIG_FILE_NAME));
                }
                sets.add(new Source.NamedSourceSet(Source.Set.set(setName),
                    Arrays.stream(setOpt.get().split(",")).map(String::trim).toArray(String[]::new)));
            }
        }
        return sets;
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
