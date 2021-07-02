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


    public static String[] instance(Processor processor){
        final var importsOp = processor.getRegister().getUserDefined(ImportsHolder.NAME).filter(c -> c instanceof ObjectHolder<?>);
        final String[] imports;
        if (importsOp.isPresent()) {
            imports = (String[]) ((ObjectHolder<?>) importsOp.get()).getObject();
        } else {
            imports = null;
        }
        return imports;
    }

}
