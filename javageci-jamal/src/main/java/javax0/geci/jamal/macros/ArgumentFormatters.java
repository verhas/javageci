package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

/**
 * Contains a few utility inner classes implementing macros that help format arguments for Java code.
 * <p>
 * Each of these macros convert the argument type list to some format that is to be used in the generated code. The
 * argument type list in the macro input. It is always the types of the arguments separated by ":" characters.
 * <p>
 * The output of the macros are comma separated list of the transformed argument type list.
 * <p>
 * When the input starts with a comma then the returned string will also contain a comma character at the start if there
 * is any element in the list of the argument types. This makes it easy to add the argument list to the end of an
 * already existing argument list. For example when calling a method using reflection then the first argument is the
 * object on which the method is invoked, and the rest of the arguments are the expresions for the values.
 * <p>
 * For an example we can have a look at a line in the file {@code unittestproxy.jim}
 * <pre>{@code
 * m.invoke(sut{%`#callArgs ,$args%});
 * }</pre>
 * <p>
 * The first argument is the variable {@code sut} and it is followed by the actual arguments. If there is no argument
 * then there must not be a comma after the variable {@code sut}. If there are values then the comma has to be there.
 * For this reason the macro {@code callArgs} contains {@code ,$args} as argument.
 * <p>
 * The individual documentation of the macro classes define only the transformation on one argument.
 */
public class ArgumentFormatters {

    private static void normalize(String[] typeNames) {
        for (int i = 0; i < typeNames.length; i++) {
            typeNames[i] = typeNames[i].trim();
            if (typeNames[i].startsWith("java.lang.")) {
                typeNames[i] = typeNames[i].substring("java.lang.".length());
            }
        }
    }

    private static abstract class ArgConverter implements Macro {

        protected abstract String argConverter(String s, int position);

        @Override
        public String evaluate(Input in, Processor processor) throws BadSyntax {
            InputHandler.skipWhiteSpaces(in);
            final boolean startWithComma = InputHandler.firstCharIs(in, ',');
            if (startWithComma) {
                InputHandler.skip(in, 1);
            }
            final var typeNames = in.toString().split(":", -1);
            if (typeNames.length == 1 && typeNames[0].length() == 0) {
                return "";
            }
            normalize(typeNames);
            for (int i = 0; i < typeNames.length; i++) {
                typeNames[i] = argConverter(typeNames[i], i);
            }
            final var sb = new StringBuilder();
            int i = 1;
            var sep = startWithComma ? "," : "";
            for (final var typeName : typeNames) {
                sb.append(sep).append(typeName);
                sep = ",";
            }
            return sb.toString();
        }
    }

    /**
     * Convert the argument types to {@code type argN} format, where {@code type} is the type in the original list,
     * {@code arg} is literally the characters {@code a}, {@code r}, {@code g} and {@code N} is an index number of the
     * argument starting from 0.
     * <p>
     * This macro should be used when a method is defined. This will declare a method with the same argument list.
     * <p>
     * See {@link ArgumentFormatters}
     */
    public static class ArgList extends ArgConverter {
        @Override
        public String getId() {
            return "argList";
        }

        @Override
        protected String argConverter(String s, int position) {
            return s + " arg" + position;
        }
    }

    /**
     * Convert the argument types to {@code argN} format, where {@code arg} is literally the characters {@code a},
     * {@code r}, {@code g} and {@code N} is an index number of the argument starting from 0.
     * <p>
     * This macro should be used when a method is invoked inside a method that was defined using the macro {@link
     * ArgList}.
     * <p>
     * See {@link ArgumentFormatters}
     */
    public static class CallArgs extends ArgConverter {
        @Override
        public String getId() {
            return "callArgs";
        }

        @Override
        protected String argConverter(String s, int position) {
            return "arg" + position;
        }
    }

    /**
     * Convert the argument types to {@code type.class} format, where {@code type} is the type in the original list. If
     * the type ends with three dots, then the dots are replaced with the character{@code []}. This is to handle the
     * varag methods properly.
     * <p>
     * This macro should be used when a method is queried via reflection and the call to {@link
     * java.lang.Class#getDeclaredMethod(String, Class[]) getDeclaredMethod(String, Class[])} or to {@link
     * Class#getMethod(String, Class[]) getMethod(String, Class[])} and the call needs the argument type list.
     * <p>
     * See {@link ArgumentFormatters}
     */
    public static class ClassList extends ArgConverter {
        @Override
        public String getId() {
            return "classList";
        }

        @Override
        protected String argConverter(String s, int position) {
            if (s.endsWith("...")) {
                return s.substring(0, s.length() - 3) + "[].class";
            } else {
                return s + ".class";
            }
        }
    }
}
