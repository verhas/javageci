package javax0.geci.tests.fluent;

import javax0.geci.annotations.Geci;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Geci("fluent definedBy='javax0.geci.buildfluent.ReplCommandBuilderFluenterTest::sourceBuilderGrammar'")
public class ReplCommandBuilder {
    private String keyword;
    private Set<String> parameters;
    private Consumer<CommandEnvironment> executor;
    private Map<String, Pattern> regexes;
    private String usage;
    private String help;

    public void kw(String keyword) {
        this.keyword = keyword;
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

    public CommandDefinition build() {
        return new CommandDefinition();
    }
    
    public static class CommandDefinition {
    }

    public static class CommandEnvironment {
    }

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    public static If10 start(){
        return new Wrapper();
    }
    public static class Wrapper implements CommandDefinitionBuilderReady,If0,If2,If1,If4,If3,If6,If5,If8,If7,If9,If10{
        private final javax0.geci.tests.fluent.ReplCommandBuilder that;
        public Wrapper(){
            this.that = new javax0.geci.tests.fluent.ReplCommandBuilder();
        }
        public Wrapper usage(String arg1){
            that.usage(arg1);
            return this;
        }
        public Wrapper help(String arg1){
            that.help(arg1);
            return this;
        }
        public Wrapper noParameters(){
            that.noParameters();
            return this;
        }
        public Wrapper kw(String arg1){
            that.kw(arg1);
            return this;
        }
        public javax0.geci.tests.fluent.ReplCommandBuilder.CommandDefinition build(){
            return that.build();
        }
        public Wrapper parameters(java.util.Set<String> arg1){
            that.parameters(arg1);
            return this;
        }
        public Wrapper regex(String arg1, String arg2){
            that.regex(arg1,arg2);
            return this;
        }
        public Wrapper parameter(String arg1){
            that.parameter(arg1);
            return this;
        }
        public Wrapper executor(java.util.function.Consumer<javax0.geci.tests.fluent.ReplCommandBuilder.CommandEnvironment> arg1){
            that.executor(arg1);
            return this;
        }
    }
    public interface CommandDefinitionBuilderReady {
        javax0.geci.tests.fluent.ReplCommandBuilder.CommandDefinition build();
    }
    public interface If0 {
        CommandDefinitionBuilderReady executor(java.util.function.Consumer<javax0.geci.tests.fluent.ReplCommandBuilder.CommandEnvironment> arg1);
    }
    public interface If1 {
        If0 help(String arg1);
    }
    public interface If2 {
        If1 usage(String arg1);
    }
    public interface If3 extends If2 {
        If3 regex(String arg1, String arg2);
    }
    public interface If5 {
        If3 noParameters();
    }
    public interface If6 {
        If3 parameters(java.util.Set<String> arg1);
    }
    public interface If7 extends If3 {
        If7 parameter(String arg1);
    }
    public interface If8 {
        If7 parameter(String arg1);
    }
    public interface If9 extends If6,If5,If8{
    }
    public interface If4 extends If3,If9 {}
    public interface If10 {
        If4 kw(String arg1);
    }
    //</editor-fold>

}
