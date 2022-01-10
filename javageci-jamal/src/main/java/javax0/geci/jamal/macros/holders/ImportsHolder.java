package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.Processor;

public class ImportsHolder extends Holder<String[]> {
    public static final String NAME = "$imports";

    public ImportsHolder(String[] imports) {
        super(imports);
    }

    @Override
    public String getId() {
        return NAME;
    }

    @Override
    public String[] getObject() {
        return object;
    }


    public static String[] instance(Processor processor) {
        final String[] imports = processor.getRegister()
            .getUserDefined(ImportsHolder.NAME)
            .filter(c -> c instanceof ObjectHolder<?>)
            .map(identified -> (String[]) ((ObjectHolder<?>) identified).getObject())
            .orElse(null);
        return imports;
    }

}
