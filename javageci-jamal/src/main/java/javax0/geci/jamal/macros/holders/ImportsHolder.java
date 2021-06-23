package javax0.geci.jamal.macros.holders;

public class ImportsHolder extends Holder<String[]> {

    public ImportsHolder(String[] imports) {
        super(imports);
    }

    @Override
    public String getId() {
        return "`imports";
    }

    @Override
    public String[] getObject() {
        return object;
    }
}
