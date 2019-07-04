package javax0.geci.docugen;

import java.util.HashMap;
import java.util.Map;

public class SnippetStore {
    final Map<String, Snippet> originals = new HashMap<>();
    final Map<String, Map<String, Snippet>> locals = new HashMap<>();

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
