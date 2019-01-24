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

    private void kw(String keyword) {
        this.keyword = keyword;
    }

    private void executor(Consumer<CommandEnvironment> executor) {
        this.executor = executor;
    }

    private void usage(String usage) {
        this.usage = usage;
    }

    private void help(String help) {
        this.help = help;
    }

    private void parameters(Set<String> parameters) {
        if (this.parameters == null) {
            this.parameters = new HashSet<>(parameters);
        } else {
            this.parameters.addAll(parameters);
        }
    }

    private void noParameters() {
        if (parameters == null) {
            this.parameters = new HashSet<>(Set.of());
        } else {
            throw new IllegalArgumentException(
                    "You cannot define parameters and noParameters for the same command");
        }
    }

    private void parameter(String parameter) {
        if (parameters == null) {
            this.parameters = new HashSet<>(Set.of(parameter));
        } else {
            this.parameters.add(parameter);
        }
    }

    private void regex(String name, String regex) {
        if (regexes == null) {
            this.regexes = new HashMap<>();
        }
        regexes.put(name, Pattern.compile(regex));
    }

    private CommandDefinition build() {
        return new CommandDefinition();
    }
    
    public static class CommandDefinition {
    }

    public static class CommandEnvironment {
    }

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    public static If26 start(){
        return new Wrapper();
    }
    public static class Wrapper implements If16,If17,If14,If15,If18,If19,If0,If2,If1,If4,If3,If6,If5,If20,If8,If7,If23,If9,If24,If21,If22,If25,If26,If12,If13,If10,If11{
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
    public interface If0 {
        javax0.geci.tests.fluent.ReplCommandBuilder.CommandDefinition build();
    }
    public interface If1 {
        If0 executor(java.util.function.Consumer<javax0.geci.tests.fluent.ReplCommandBuilder.CommandEnvironment> arg1);
    }
    public interface If2 {
        If1 help(String arg1);
    }
    public interface If3 {
        If2 usage(String arg1);
    }
    public interface If4 extends If3 {
        If4 regex(String arg1, String arg2);
    }
    public interface If6 {
        If4 kw(String arg1);
    }
    public interface If5 extends If4,If6 {}
    public interface If7 {
        If5 noParameters();
    }
    public interface If8 {
        If5 parameters(java.util.Set<String> arg1);
    }
    public interface If9 extends If5 {
        If9 parameter(String arg1);
    }
    public interface If10 {
        If9 parameter(String arg1);
    }
    public interface If11 extends If8,If7,If10{
    }
    public interface If12 {
        If11 noParameters();
    }
    public interface If13 {
        If12 kw(String arg1);
    }
    public interface If14 {
        If13 executor(java.util.function.Consumer<javax0.geci.tests.fluent.ReplCommandBuilder.CommandEnvironment> arg1);
    }
    public interface If15 {
        If14 help(String arg1);
    }
    public interface If16 {
        If15 usage(String arg1);
    }
    public interface If17 extends If16 {
        If17 regex(String arg1, String arg2);
    }
    public interface If19 {
        If17 kw(String arg1);
    }
    public interface If18 extends If17,If19 {}
    public interface If20 {
        If18 noParameters();
    }
    public interface If21 {
        If18 parameters(java.util.Set<String> arg1);
    }
    public interface If22 extends If18 {
        If22 parameter(String arg1);
    }
    public interface If23 {
        If22 parameter(String arg1);
    }
    public interface If24 extends If20,If23,If21{
    }
    public interface If25 {
        If24 noParameters();
    }
    public interface If26 {
        If25 kw(String arg1);
    }
    //</editor-fold>

}
