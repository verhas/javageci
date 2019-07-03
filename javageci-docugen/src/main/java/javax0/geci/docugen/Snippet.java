package javax0.geci.docugen;

import javax0.geci.tools.CompoundParams;

import java.util.List;
import java.util.Set;

public class Snippet {
    final private CompoundParams params;
    final private List<String> lines;

    public Snippet(CompoundParams params, List<String> lines) {
        this.params = params;
        this.lines = lines;
    }

    public String param(String s) {
        return params.get(s);
    }

    public Set<String> keys() {
        return params.keySet();
    }
}
