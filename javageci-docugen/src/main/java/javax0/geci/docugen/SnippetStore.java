package javax0.geci.docugen;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SnippetStore {
    final Map<String, Snippet> originals = new HashMap<>();
    final Map<String, Map<String, Snippet>> locals = new HashMap<>();
    final Map<String, Source> sourceTracking = new HashMap<>();

    private static final String EPSILON = "epsilon";

    SnippetStore() {
        originals.put(EPSILON, new Snippet(EPSILON, new CompoundParams(EPSILON, Map.of()), List.of()));
    }

    //snippet SnippetStore_name
    Set<String> names() {
        return originals.keySet();
    }
    // end snippet

    /**
     * Store a new snippet in the store.
     *
     * @param name    the name of the snippet
     * @param snippet the snippet
     * @param source  the source where the snippet is defined. It is
     *                used to report errors in case a snippet is defined multiple
     *                times. In that case the first and the second source is reported.
     *                Other sources are not discovered because the second time a
     *                snippet is to be stored with the same name an exception is
     *                thrown.
     */
    void put(String name, Snippet snippet, Source source) {
        if (originals.containsKey(name)) {
            throw new GeciException("Snippet '" + name + "' is defined multiple times in sources\n" +
                    source.getAbsoluteFile() + "\n" + sourceTracking.get(name).getAbsoluteFile());
        }
        originals.put(name, snippet);
        sourceTracking.put(name, source);
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
