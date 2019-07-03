package javax0.geci.docugen;

import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.CompoundParamsBuilder;

import java.util.ArrayList;
import java.util.List;

public class SnippetBuilder extends CompoundParams {
    private final CompoundParams params;
    private List<String> lines = new ArrayList<>();

    public String snippetName() {
        return params.id();
    }

    public Snippet build() {
        return new Snippet(params, lines);
    }

    SnippetBuilder(String s) {
        params = new CompoundParamsBuilder(s).build();
    }

    SnippetBuilder add(String line) {
        lines.add(line);
        return this;
    }
}
