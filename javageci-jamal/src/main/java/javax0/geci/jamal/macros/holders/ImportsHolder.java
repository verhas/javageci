package javax0.geci.jamal.macros.holders;

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
}
