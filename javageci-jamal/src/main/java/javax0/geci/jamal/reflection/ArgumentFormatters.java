package javax0.geci.jamal.reflection;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

/**
 * Contains a few utility inner classes implementing macro that help formatting arguments for Java code.
 * <p>
 * An argument list, the input is always the types of the arguments separated by ":" characters.
 * <p>
 * The oputput of the macros are different formats created from it.
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
            if( typeNames.length == 1 && typeNames[0].length() == 0 ){
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

    public static class ClassList extends ArgConverter {
        @Override
        public String getId() {
            return "classList";
        }

        @Override
        protected String argConverter(String s, int position) {
            return s + ".class";
        }
    }
}
