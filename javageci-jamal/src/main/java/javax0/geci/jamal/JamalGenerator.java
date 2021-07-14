package javax0.geci.jamal;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.jamal.macros.holders.ImportsHolder;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.geci.tools.CompoundParamsBuilder;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Position;
import javax0.jamal.engine.Processor;
import javax0.jamal.tools.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A code generator class that reads the source files and works up all segments that look like
 *
 * <pre>{@code
 *     /*!jamal
 *      ...
 *      ... template part inside Java comment
 *      ...
 *      *\u0000/}{@code
 *      ...
 *      ... code part
 *      ...
 *      //__END__}
 * </pre>
 * <p>
 * reading and using the 'template part' as Jamal macro source and processing it and replacing with the
 * result the 'code part'.
 */
public class JamalGenerator extends AbstractGeneratorEx {
    private static final Pattern START = Pattern.compile("^\\s*/\\*!(jamal\\s*.*)$");
    private static final Pattern COMMENT_END = Pattern.compile("^\\s*\\*/\\s*$");
    private static final Pattern SEGMENT_END = Pattern.compile("^\\s*//\\s*__END__\\s*$");
    private static final Pattern IMPORT = Pattern.compile("^\\s*import\\s*(.*);\\s*$");
    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\\s*(.*);\\s*$");
    private static final Pattern CLASS_START = Pattern.compile("^\\s*(:?private\\s+|protected\\s+|public\\s+)?class\\s+.*$");

    @Override
    public void processEx(Source source) {
        Processor processor = null;
        var lines = source.getLines();
        final var imports = getImports(lines);
        var touched = false;
        final var sb = new StringBuilder();
        var state = PROCESSING.COPY;
        final var macro = new StringBuilder();
        var lineNr = 0;
        int positionLineNr = 0;
        var optionsLine = "";
        for (final var line : lines) {
            lineNr++;
            switch (state) {
                case COPY:
                    sb.append(line).append("\n");
                    final var startMatcher = START.matcher(line);
                    if (startMatcher.matches()) {
                        optionsLine = startMatcher.group(1);
                        touched = true;
                        state = PROCESSING.INSOURCE;
                        macro.delete(0, macro.length());
                        positionLineNr = lineNr + 1;
                    }
                    break;
                case INSOURCE:
                    sb.append(line).append("\n");
                    if (COMMENT_END.matcher(line).matches()) {
                        state = PROCESSING.OUTPUT;
                        final String result;
                        try {
                            if (processor == null) {
                                CompoundParams params = new CompoundParamsBuilder(optionsLine).build();
                                final var debug = params.get("debug", "");
                                try {
                                    System.setProperty("jamal.debug", debug);
                                    processor = new Processor("{%", "%}");
                                    processor.define(new ImportsHolder(imports.toArray(String[]::new)));
                                } catch (IllegalArgumentException ex) {
                                    throw new GeciException("Jamal processor opening threw exception", ex);
                                }
                            }
                            result = processor.process(new Input(macro.toString(),
                                new Position(source.getAbsoluteFile(), positionLineNr)));
                            macro.delete(0, macro.length());
                        } catch (BadSyntax badSyntax) {
                            throw new GeciException("Macro processing in file '"
                                + source.getAbsoluteFile() + "' threw exception", badSyntax);
                        }
                        sb.append(result);
                    } else {
                        macro.append(line).append("\n");
                    }
                    break;
                case OUTPUT:
                    if (SEGMENT_END.matcher(line).matches()) {
                        sb.append(line).append("\n");
                        state = PROCESSING.COPY;
                    }
                    break;
            }
        }
        if (touched) {
            for (int i = sb.length() - 1; i >= 0 && sb.charAt(i) == '\n'; i--) {
                sb.deleteCharAt(i);
            }
            try (final var output = source.open()) {
                output.write(sb.toString());
            }
        }
        if (processor != null) {
            processor.close();
        }
    }

    /**
     * Collect all the classes that are imported. These are added to the user defined macro "$imports" used by the
     * built-in macros {@code Fields} and {@code Methods}. That way these macros know which class names need fully
     * qualified package name, and which are imported in to the class and do not need the full name.
     *
     * @param lines the input lines of the source code
     * @return the List of the strings, each representing a class or a set of classes in case the import uses wild card
     */
    private List<String> getImports(List<String> lines) {
        var imports = new ArrayList<String>();
        for (final var line : lines) {
            addPackageToImports(imports, line);
            addImportedClassToImports(imports, line);
            if (CLASS_START.matcher(line).matches()) {
                break;
            }
        }
        return imports;
    }

    /**
     * If the line is an import line then add the imported class to the array as a string exactly as it is after the
     * keyword {@code import}.
     *
     * @param imports the imports to add the package name to
     * @param line    the line that may contain the package declaration
     */
    private void addImportedClassToImports(ArrayList<String> imports, String line) {
        final var importMatcher = IMPORT.matcher(line);
        if (importMatcher.matches()) {
            imports.add(importMatcher.group(1));
        }
    }

    /**
     * If the line is the package declaration then add the name of the package and the {@code .*} ending to the
     * list of imports. Any class, which is in the same package as the current class is imported implicitly.
     * <p>
     * If the line is NOT the package declaration, just ignore it.
     *
     * @param imports the imports to add the package name to
     * @param line    the line that may contain the package declaration
     */
    private void addPackageToImports(ArrayList<String> imports, String line) {
        final var packageMatcher = PACKAGE.matcher(line);
        if (packageMatcher.matches()) {
            imports.add(packageMatcher.group(1) + ".*");
        }
    }

    private enum PROCESSING {
        COPY, INSOURCE, OUTPUT
    }
}
