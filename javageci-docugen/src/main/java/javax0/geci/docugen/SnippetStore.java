package javax0.geci.docugen;

import javax0.geci.tools.CompoundParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SnippetStore {
    final Map<String, Snippet> originals = new HashMap<>();
    final Map<String, Map<String, Snippet>> locals = new HashMap<>();

    private static final String EPSILON = "epsilon";

    SnippetStore() {
        originals.put(EPSILON, new Snippet(EPSILON, new CompoundParams(EPSILON, Map.of()), List.of()));
    }

    //snippet SnippetStore_name
    Set<String> names() {
        return originals.keySet();
    }
    // end snippet

    void put(String name, Snippet snippet) {
        originals.put(name, snippet);
    }

    Snippet get(String segmentName, String snippetName) {
        if (!originals.containsKey(snippetName)) {
            return null;
        }
        if (!locals.containsKey(snippetName)) {
            locals.put(snippetName, new HashMap<>());
        }
        final var local = locals.get(snippetName);
        if (!local.containsKey(segmentName)) {
            local.put(segmentName, originals.get(snippetName).copy());
        }
        return local.get(segmentName);
    }
}
