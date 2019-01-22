package javax0.geci.tests.fluent;

import javax0.geci.annotations.Geci;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Geci("fluent definedBy='javax0.geci.buildfluent.CommandDefinitionBuilderFluenterTest::sourceBuilderGrammar'")
public class ReplCommandBuilder {
    private String keyword;
    private Set<String> parameters;
    private Consumer<CommandEnvironment> executor;
    private Map<String, Pattern> regexes;
    private String usage;
    private String help;

    public CommandDefinition build() {
        return new CommandDefinition();
    }

    public void executor(Consumer<CommandEnvironment> executor) {
        this.executor = executor;
    }

    public void usage(String usage) {
        this.usage = usage;
    }

    public void help(String help) {
        this.help = help;
    }

    public void parameters(Set<String> parameters) {
        if (this.parameters == null) {
            this.parameters = new HashSet<>(parameters);
        } else {
            this.parameters.addAll(parameters);
        }
    }

    public void noParameters() {
        if (parameters == null) {
            this.parameters = new HashSet<>(Set.of());
        } else {
            throw new IllegalArgumentException(
                    "You cannot define parameters and noParameters for the same command");
        }
    }

    public void parameter(String parameter) {
        if (parameters == null) {
            this.parameters = new HashSet<>(Set.of(parameter));
        } else {
            this.parameters.add(parameter);
        }
    }

    public void regex(String name, String regex) {
        if (regexes == null) {
            this.regexes = new HashMap<>();
        }
        regexes.put(name, Pattern.compile(regex));
    }

    public static class CommandDefinition {
    }

    public static class CommandEnvironment {
    }

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    //</editor-fold>

}
