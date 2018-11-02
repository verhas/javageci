package javax0.geci.jamal;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.engine.Processor;
import javax0.jamal.tools.Input;

import java.util.regex.Pattern;

public class JamalGenerator extends AbstractGeneratorEx {
    private enum PROCESSING {
        COPY, INSOURCE, OUTPUT
    }

    private static final Pattern START = Pattern.compile("^\\s*/\\*!jamal\\s*$");
    private static final Pattern COMMENT_END = Pattern.compile("^\\s*\\*/\\s*$");
    private static final Pattern SEGMENT_END = Pattern.compile("^\\s*//__END__\\s*$");

    @Override
    public void processEx(Source source) {
        final Processor processor;
        try {
            processor = new Processor("{{", "}}");
        } catch (BadSyntax badSyntax) {
            throw new GeciException("Jamal processor opening threw exception", badSyntax);
        }
        var lines = source.getLines();
        var output = source.open();
        var state = PROCESSING.COPY;
        var macro = new StringBuilder();
        for (final var line : lines) {
            switch (state) {
                case COPY:
                    output.write(line);
                    if (START.matcher(line).matches()) {
                        state = PROCESSING.INSOURCE;
                        macro.delete(0, macro.length());
                    }
                    break;
                case INSOURCE:
                    output.write(line);
                    if (COMMENT_END.matcher(line).matches()) {
                        state = PROCESSING.OUTPUT;
                        final String result;
                        try {
                            result = processor.process(new Input(macro, source.getAbsoluteFile()));
                        } catch (BadSyntax badSyntax) {
                            throw new GeciException("Macro processing in file '"
                                    + source.getAbsoluteFile() + "' threw exception", badSyntax);
                        }
                        output.write(result);
                    } else {
                        macro.append(line).append("\n");
                    }
                    break;
                case OUTPUT:
                    if (SEGMENT_END.matcher(line).matches()) {
                        output.write(line);
                        state = PROCESSING.COPY;
                    }
                    break;
            }
        }
    }
}
