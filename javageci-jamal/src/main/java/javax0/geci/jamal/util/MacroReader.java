package javax0.geci.jamal.util;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Evaluable;
import javax0.jamal.api.Processor;

import java.util.Optional;

public class MacroReader {
    final Processor processor;

    public MacroReader(Processor processor) {
        this.processor = processor;
    }

    public Optional<String> readValue(String macro) {
        return processor.getRegister().getUserDefined(macro)
            .filter(ud -> ud instanceof Evaluable)
            .map(ud -> (Evaluable) ud)
            .map(udm -> {
                try {
                    return udm.evaluate();
                } catch (BadSyntax bs) {
                    return null;
                }
            });
    }
}
