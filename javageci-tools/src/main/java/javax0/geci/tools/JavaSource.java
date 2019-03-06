package javax0.geci.tools;

import javax0.geci.annotations.Geci;

import java.util.Arrays;

/**
 * A simple tool to write code into a string.
 */
@Geci("fluent definedBy='javax0.geci.buildfluent.BuildFluentForSourceBuilder::sourceBuilderGrammar'")
public class JavaSource implements AutoCloseable {
    private static final int TAB = 4;
    private final StringBuilder code = new StringBuilder();
    private final MethodSetup lastMethod = new MethodSetup();
    private int tabStop = 0;

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    public interface Builder extends If24 {
    }

    public static Builder builder() {
        return new Wrapper();
    }

    public static class Wrapper implements If16, If17, If14, If15, If18, If19, Builder, If0, If2, If1, If4, If3, If6, If5, If20, If8, If7, If23, If9, If24, If21, If22, AutoCloseable, MethodBody, If12, If13, If10, If11 {
        private final javax0.geci.tools.JavaSource that;

        public Wrapper() {
            this.that = new javax0.geci.tools.JavaSource();
        }

        public Wrapper ifStatement(String arg1, Object... arg2) {
            that.ifStatement(arg1, arg2);
            return this;
        }

        public Wrapper statement(String arg1, Object... arg2) {
            that.statement(arg1, arg2);
            return this;
        }

        public Wrapper returnStatement() {
            that.returnStatement();
            return this;
        }

        public Wrapper forStatement(String arg1, Object... arg2) {
            that.forStatement(arg1, arg2);
            return this;
        }

        public Wrapper write_r(String arg1, Object... arg2) {
            that.write_r(arg1, arg2);
            return this;
        }

        public String toString() {
            return that.toString();
        }

        public Wrapper open(String arg1, Object... arg2) {
            that.open(arg1, arg2);
            return this;
        }

        public Wrapper elseStatement() {
            that.elseStatement();
            return this;
        }

        public Wrapper method(String arg1) {
            that.method(arg1);
            return this;
        }

        public Wrapper noArgs() {
            that.noArgs();
            return this;
        }

        public Wrapper returnStatement(String arg1, Object... arg2) {
            that.returnStatement(arg1, arg2);
            return this;
        }

        public Wrapper comment(String arg1, Object... arg2) {
            that.comment(arg1, arg2);
            return this;
        }

        public Wrapper modifiers(String arg1) {
            that.modifiers(arg1);
            return this;
        }

        public Wrapper args(String... arg1) {
            that.args(arg1);
            return this;
        }

        public Wrapper write_l(String arg1, Object... arg2) {
            that.write_l(arg1, arg2);
            return this;
        }

        public Wrapper whileStatement(String arg1, Object... arg2) {
            that.whileStatement(arg1, arg2);
            return this;
        }

        public Wrapper exceptions(String arg1) {
            that.exceptions(arg1);
            return this;
        }

        public Wrapper returnType(String arg1) {
            that.returnType(arg1);
            return this;
        }

        public Wrapper write_lr(String arg1, Object... arg2) {
            that.write_lr(arg1, arg2);
            return this;
        }

        public Wrapper write(String arg1, Object... arg2) {
            that.write(arg1, arg2);
            return this;
        }

        public void close() {
            that.close();
        }

        public Wrapper newline() {
            that.newline();
            return this;
        }
    }

    public interface If0 {
        String toString();
    }

    public interface If2 {
        If1 comment(String arg1, Object... arg2);

        If1 statement(String arg1, Object... arg2);

        If1 write(String arg1, Object... arg2);

        If1 write_r(String arg1, Object... arg2);

        If1 write_l(String arg1, Object... arg2);

        If1 newline();

        If1 open(String arg1, Object... arg2);
    }

    public interface If5 {
        If4 comment(String arg1, Object... arg2);

        If4 statement(String arg1, Object... arg2);

        If4 write(String arg1, Object... arg2);

        If4 write_r(String arg1, Object... arg2);

        If4 write_l(String arg1, Object... arg2);

        If4 newline();

        If4 open(String arg1, Object... arg2);
    }

    public interface If4 extends If1, AutoCloseable, If5 {
    }

    public interface If6 {
        If4 elseStatement();
    }

    public interface If3 extends If1, AutoCloseable, If6 {
    }

    public interface If8 {
        If7 comment(String arg1, Object... arg2);

        If7 statement(String arg1, Object... arg2);

        If7 write(String arg1, Object... arg2);

        If7 write_r(String arg1, Object... arg2);

        If7 write_l(String arg1, Object... arg2);

        If7 newline();

        If7 open(String arg1, Object... arg2);
    }

    public interface If7 extends If3, AutoCloseable, If8 {
    }

    public interface If9 {
        If7 ifStatement(String arg1, Object... arg2);
    }

    public interface If11 {
        If10 comment(String arg1, Object... arg2);

        If10 statement(String arg1, Object... arg2);

        If10 write(String arg1, Object... arg2);

        If10 write_r(String arg1, Object... arg2);

        If10 write_l(String arg1, Object... arg2);

        If10 newline();

        If10 open(String arg1, Object... arg2);
    }

    public interface If10 extends If1, AutoCloseable, If11 {
    }

    public interface If12 {
        If10 whileStatement(String arg1, Object... arg2);
    }

    public interface If14 {
        If13 comment(String arg1, Object... arg2);

        If13 statement(String arg1, Object... arg2);

        If13 write(String arg1, Object... arg2);

        If13 write_r(String arg1, Object... arg2);

        If13 write_l(String arg1, Object... arg2);

        If13 newline();

        If13 open(String arg1, Object... arg2);
    }

    public interface If13 extends If14, If1, AutoCloseable {
    }

    public interface If15 {
        If13 forStatement(String arg1, Object... arg2);
    }

    public interface If16 {
        MethodBody comment(String arg1, Object... arg2);

        MethodBody statement(String arg1, Object... arg2);

        MethodBody write(String arg1, Object... arg2);

        MethodBody write_r(String arg1, Object... arg2);

        MethodBody write_l(String arg1, Object... arg2);

        MethodBody newline();

        MethodBody open(String arg1, Object... arg2);
    }

    public interface If17 {
        MethodBody returnStatement();

        MethodBody returnStatement(String arg1, Object... arg2);
    }

    public interface If18 extends If16, If17, AutoCloseable {
    }

    public interface MethodBody extends If1, If18, AutoCloseable {
    }

    public interface If19 {
        MethodBody noArgs();

        MethodBody args(String... arg1);
    }

    public interface If20 extends If19, AutoCloseable {
        If19 exceptions(String arg1);
    }

    public interface If21 extends AutoCloseable, If20 {
        If20 returnType(String arg1);
    }

    public interface If22 extends AutoCloseable, If21 {
        If21 modifiers(String arg1);
    }

    public interface If23 {
        If22 method(String arg1);
    }

    public interface If24 extends If15, If2, AutoCloseable, If12, If23, If9 {
    }

    public interface If1 extends If0, AutoCloseable, If24 {
    }
    //</editor-fold>

    /**
     * Write a string with format placeholders into the underlying string. If the string is multi line (contains \n)
     * then each line will be aligned to the actual tab stop.
     * <p>
     * If the line is terminated with one or more \n characters then they will be removed and the line will be
     * terminated with a single new line character.
     *
     * @param s          the format string
     * @param parameters optional parameters
     * @return {@code this}
     */
    public JavaSource write(String s, Object... parameters) {
        if (s.trim().length() == 0) {
            newline();
        } else {
            var formatted = String.format(s, parameters);
            if (formatted.contains("\n")) {
                Arrays.stream(formatted.split("\n")).forEach(this::write);
            } else {
                formatted = formatted.replaceAll("\n+$", "");
                code.append(tabStop > 0 ? String.format("%" + tabStop + "s", " ") : "").append(formatted).append("\n");
            }
        }
        return this;
    }

    /**
     * Add a new line to the
     * @return {@code this}
     */
    public JavaSource newline() {
        code.append("\n");
        return this;

    }

    /**
     * Write the string to the code and then increase the tab stop.
     *
     * @param s          the format string
     * @param parameters optional parameters
     * @return {@code this}
     */
    public JavaSource write_r(String s, Object... parameters) {
        write(s, parameters);
        tabStop += TAB;
        return this;
    }

    /**
     * Decrease the tab stop and then write the string to the code.
     *
     * @param s          the format string
     * @param parameters optional parameters
     * @return {@code this}
     */
    public JavaSource write_l(String s, Object... parameters) {
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
     * @return {@code this}
     */
    public JavaSource write_lr(String s, Object... parameters) {
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

    public JavaSource method(String name) {
        lastMethod.name = name;
        lastMethod.type = "void";
        lastMethod.modifiers = "";
        lastMethod.exceptions = "";
        return this;
    }

    public JavaSource modifiers(String modifiers) {
        lastMethod.modifiers = modifiers;
        return this;
    }

    public JavaSource returnType(String type) {
        lastMethod.type = type;
        return this;
    }

    public JavaSource exceptions(String exceptions) {
        lastMethod.exceptions = exceptions;
        return this;
    }

    public JavaSource args(String... args) {
        var argList = String.join(",", args);
        var sb = new StringBuilder();
        if (lastMethod.modifiers != null && lastMethod.modifiers.length() > 0) {
            sb.append(lastMethod.modifiers).append(" ");
        }
        sb.append(lastMethod.type).append(" ");
        sb.append(lastMethod.name).append("(").append(argList).append(")");
        if (lastMethod.exceptions != null && lastMethod.exceptions.length() > 0) {
            sb.append(" throws ").append(lastMethod.exceptions);
        }
        return open(sb.toString());
    }

    public JavaSource noArgs() {
        return args();
    }

    /**
     * You can use this method in the try-with-resources output
     *
     * @param s          the line that opens the block without the {@code $&#123;}at the end of the line. That will automatically
     *                   be appended.
     * @param parameters parameters of the line
     * @return {@code this}
     */
    public JavaSource open(String s, Object... parameters) {
        s += "{";
        write_r(s, parameters);
        return this;
    }

    public JavaSource comment(String s, Object... parameters) {
        s = "// " + s;
        write(s, parameters);
        return this;
    }

    public JavaSource statement(String s, Object... parameters) {
        s += ";";
        write(s, parameters);
        return this;
    }

    public JavaSource whileStatement(String s, Object... parameters) {
        s = "while( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSource forStatement(String s, Object... parameters) {
        s = "for( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSource ifStatement(String s, Object... parameters) {
        s = "if( " + s + " ){";
        write_r(s, parameters);
        return this;
    }

    public JavaSource elseStatement() {
        write_lr("}else{");
        return this;
    }

    public JavaSource returnStatement() {
        statement("return");
        return this;
    }

    public JavaSource returnStatement(String s, Object... parameters) {
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
