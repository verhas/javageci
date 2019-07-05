package javax0.geci.docugen;

import javax0.geci.tools.CompoundParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Snippet {
    final private String name;
    final private CompoundParams params;
    final private List<String> lines;

    public Snippet copy() {
        return new Snippet(name, params, new ArrayList<>(lines));
    }

    public Snippet(String name, CompoundParams params, List<String> lines) {
        this.name = name;
        this.params = params;
        this.lines = lines;
    }

    public String name() {
        return name;
    }

    public String param(String s) {
        return params.get(s);
    }

    public Set<String> keys() {
        return params.keySet();
    }

    public List<String> lines() {
        return lines;
    }
}
