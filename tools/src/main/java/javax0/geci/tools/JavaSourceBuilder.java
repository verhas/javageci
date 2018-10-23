package javax0.geci.tools;

import javax0.geci.annotations.Geci;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A simple tool to write code into a string.
 */
@Geci("fluent definedBy='javax0.geci.buildfluent.BuildFluentForSourceBuilder::sourceBuilderGrammar'")
public class JavaSourceBuilder implements AutoCloseable {
    private static final int TAB = 4;
    private final StringBuilder code = new StringBuilder();
    private final MethodSetup lastMethod = new MethodSetup();
    private int tabStop = 0;

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    interface JavaBuilder extends If0 {}
    public static JavaBuilder source(){
        return new Wrapper();
    }
    public static class Wrapper implements JavaBuilder,If0,AutoCloseable{
        private final javax0.geci.tools.JavaSourceBuilder that;
        public Wrapper(javax0.geci.tools.JavaSourceBuilder that){
            this.that = that;
        }
        public Wrapper(){
            this.that = new javax0.geci.tools.JavaSourceBuilder();
        }
        public Wrapper ifStatement(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.ifStatement(arg1,arg2);
            return next;
        }
        public Wrapper statement(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.statement(arg1,arg2);
            return next;
        }
        public Wrapper returnStatement(){
            var next = new Wrapper(that.copy());
            next.that.returnStatement();
            return next;
        }
        public Wrapper forStatement(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.forStatement(arg1,arg2);
            return next;
        }
        public Wrapper write_r(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.write_r(arg1,arg2);
            return next;
        }
        public String toString(){
            return that.toString();
        }
        public Wrapper elseStatement(){
            var next = new Wrapper(that.copy());
            next.that.elseStatement();
            return next;
        }
        public Wrapper open(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.open(arg1,arg2);
            return next;
        }
        public Wrapper method(String arg1){
            var next = new Wrapper(that.copy());
            next.that.method(arg1);
            return next;
        }
        public Wrapper noArgs(){
            var next = new Wrapper(that.copy());
            next.that.noArgs();
            return next;
        }
        public Wrapper returnStatement(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.returnStatement(arg1,arg2);
            return next;
        }
        public Wrapper comment(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.comment(arg1,arg2);
            return next;
        }
        public Wrapper modifiers(String arg1){
            var next = new Wrapper(that.copy());
            next.that.modifiers(arg1);
            return next;
        }
        public Wrapper args(String[] arg1){
            var next = new Wrapper(that.copy());
            next.that.args(arg1);
            return next;
        }
        public Wrapper write_l(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.write_l(arg1,arg2);
            return next;
        }
        public Wrapper whileStatement(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.whileStatement(arg1,arg2);
            return next;
        }
        public Wrapper exceptions(String arg1){
            var next = new Wrapper(that.copy());
            next.that.exceptions(arg1);
            return next;
        }
        public Wrapper returnType(String arg1){
            var next = new Wrapper(that.copy());
            next.that.returnType(arg1);
            return next;
        }
        public Wrapper write_lr(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.write_lr(arg1,arg2);
            return next;
        }
        public Wrapper write(String arg1,Object[] arg2){
            var next = new Wrapper(that.copy());
            next.that.write(arg1,arg2);
            return next;
        }
        public Wrapper newline(){
            var next = new Wrapper(that.copy());
            next.that.newline();
            return next;
        }
        public void close(){
            that.close();
        }
    }
    interface If0 {
        String toString();
    }
    //</editor-fold>

    public JavaSourceBuilder copy() {
        var next = new JavaSourceBuilder();
        next.code.append(code);
        next.tabStop = tabStop;
        return next;
    }

    /**
     * Write a string with format placeholders into the underlying string. If the string is multi line (contains \n)
     * then each line will be aligned to the actual tab stop.
     * <p>
     * If the line is terminated with one or more \n characters then they will be removed and the line will be
     * terminated with a single new line character.
     *
     * @param s          the format string
     * @param parameters optional parameters
     */
    public JavaSourceBuilder write(String s, Object... parameters) {
        if (s.trim().length() == 0) {
            newline();
        } else {
            var formatted = String.format(s, parameters);
            if (formatted.contains("\n")) {
                Arrays.stream(formatted.split("\n")).forEach(this::write);
            } else {
                formatted = formatted.replaceAll("\n+$", "");
                code.append(" ".repeat(tabStop)).append(formatted).append("\n");
            }
        }
        return this;
    }

    /**
     * Add a new line to the
     */
    public JavaSourceBuilder newline() {
        code.append("\n");
        return this;

    }

    /**
     * Write the string to the code and then increase the tab stop.
     *
     * @param s
     * @param parameters
     */
    public JavaSourceBuilder write_r(String s, Object... parameters) {
        write(s, parameters);
        tabStop += TAB;
        return this;
    }

    /**
     * Decrease the tab stop and then write the string to the code.
     *
     * @param s
     * @param parameters
     */
    public JavaSourceBuilder write_l(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < 0) {
            tabStop = 0;
        }
        write(s, parameters);
        return this;
    }

    /**
     * Write a line that is pulled left but then again the next to the right. Typically
     * this is {@code else} lines.
     *
     * @param s          the line to be added to the code
     * @param parameters the format parameters
     * @return this
     */
    public JavaSourceBuilder write_lr(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < 0) {
            tabStop = 0;
        }
        write_r(s, parameters);
        return this;
    }

    @Override
    public String toString() {
        return code.toString();
    }

    public JavaSourceBuilder method(String name) {
        lastMethod.name = name;
        lastMethod.type = "void";
        lastMethod.modifiers = "";
        lastMethod.exceptions = "";
        return this;
    }

    public JavaSourceBuilder modifiers(String modifiers) {
        lastMethod.modifiers = modifiers;
        return this;
    }

    public JavaSourceBuilder returnType(String type) {
        lastMethod.type = type;
        return this;
    }

    public JavaSourceBuilder exceptions(String exceptions) {
        lastMethod.exceptions = exceptions;
        return this;
    }

    public JavaSourceBuilder args(String... args) {
        var argList = String.join(",", Arrays.stream(args).collect(Collectors.toList()));
        var sb = new StringBuilder();
        if( lastMethod.modifiers.length() > 0 ){
            sb.append(lastMethod.modifiers).append(" ");
        }
        sb.append(lastMethod.type).append(" ");
        sb.append(lastMethod.name).append("(").append(argList).append(")");
        if( lastMethod.exceptions.length() > 0 ){
            sb.append(" throws ").append(lastMethod.exceptions);
        }
        return open(sb.toString());
    }

    public JavaSourceBuilder noArgs() {
        return args();
    }

    /**
     * You can use this method in the try-with-resources output
     *
     * @param s          the line that opens the block without the {@code { } at the end of the line. That will automatically
     *                   be appended.
     * @param parameters parameters of the line
     * @return this
     */
    public JavaSourceBuilder open(String s, Object... parameters) {
        s += "{";
        write_r(s, parameters);
        return this;
    }

    public JavaSourceBuilder comment(String s, Object... parameters) {
        s = "// " + s;
        write(s, parameters);
        return this;
    }

    public JavaSourceBuilder statement(String s, Object... parameters) {
        s += ";";
        write(s, parameters);
        return this;
    }

    public JavaSourceBuilder whileStatement(String s, Object... parameters) {
        s = "while( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSourceBuilder forStatement(String s, Object... parameters) {
        s = "for( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSourceBuilder ifStatement(String s, Object... parameters) {
        s = "if( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSourceBuilder elseStatement() {
        write_lr("}else{");
        return this;
    }

    public JavaSourceBuilder returnStatement() {
        statement("return");
        return this;
    }

    public JavaSourceBuilder returnStatement(String s, Object... parameters) {
        s = "return " + s;
        statement(s, parameters);
        return this;
    }

    @Override
    public void close() {
        write_l("}");
    }

    private static class MethodSetup {
        String name;
        String modifiers;
        String type;
        String exceptions;
    }
}
