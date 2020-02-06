package javax0.geci.engine;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.tools.Template;
import javax0.geci.tools.Tracer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Segment implements javax0.geci.api.Segment {
    private static final int TAB = 4;
    final List<String> preface = new LinkedList<>();
    final List<String> lines = new LinkedList<>();
    final List<String> postface = new LinkedList<>();
    final private int openingTabStop;
    private int tabStop;
    private final Map<String, String> params = new HashMap<>();
    private final CompoundParams cparams;
    private final List<String> originals;
    private long touchBits = 0;

    public Segment(int tabStop) {
        this.openingTabStop = tabStop;
        this.tabStop = tabStop;
        this.cparams = new javax0.geci.tools.CompoundParams();
        this.originals = Collections.emptyList();
    }

    public Segment(int tabStop, CompoundParams cparams, List<String> originals) {
        this.openingTabStop = tabStop;
        this.tabStop = tabStop;
        this.cparams = cparams;
        this.originals = originals;
    }

    @Override
    public long touch(long value){
        return touchBits |= value;
    }

    @Override
    public CompoundParams sourceParams() {
        return cparams;
    }

    @Override
    public void resetParams() {
        params.clear();
    }

    @Override
    public Segment param(String... keyValuePairs) {
        if (keyValuePairs.length % 2 == 1) {
            throw new IllegalArgumentException("Parameters to Segment.param() should be in pair");
        }
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            params.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return this;
    }


    @Override
    public Optional<String> getParam(String key){
        if( params.containsKey(key)){
            return Optional.of(params.get(key));
        }
        return Optional.empty();
    }

    @Override
    public Set<String> paramKeySet() {
        return params.keySet();
    }

    @Override
    public void traceParams() {
        params.forEach(Tracer::log);
    }

    @Override
    public void traceLines() {
        try (final var pos = Tracer.push("SegmentContent", null)) {
            try (final var lines = Tracer.push("Originals", null)) {
                originals.forEach(line -> Tracer.log("Line", line));
            }
            try (final var linesPos = Tracer.push("Updated", null)) {
                lines.forEach(line -> Tracer.log("Line", line));
            }
        }
    }

    @Override
    public List<String> originalLines() {
        return originals;
    }

    @Override
    public String getContent() {
        return String.join("\n", preface) +
            String.join("\n", lines) +
            String.join("\n", postface);
    }

    @Override
    public void setPreface(String... preface) {
        for (String s : preface) {
            this.preface.add((tabStop > 0 ? String.format("%" + tabStop + "s", " ") : "") + s);
        }
    }

    @Override
    public void setPostface(String... postface) {
        for (String s : postface) {
            this.postface.add((tabStop > 0 ? String.format("%" + tabStop + "s", " ") : "") + s);
        }
    }

    @Override
    public void setContent(String content) {
        lines.clear();
        lines.addAll(Arrays.asList(content.split("\n", -1)));
    }

    @Override
    public void close() {
    }

    @Override
    public Segment write(javax0.geci.api.Segment segment) {
        if (segment != null) {
            if (segment instanceof Segment) {
                var other = (Segment) segment;
                other.lines.forEach(line -> write(line));
            } else {
                throw new GeciException("Segment " + segment + " is not instance of " + Segment.class.getName() +
                    ". It is " + segment.getClass().getName() + "which is not compatible with this implementation");
            }
        }
        return this;
    }

    @Override
    public Segment write(String s, Object... parameters) {
        if (s != null) {
            if (!s.contains("\n") && s.trim().length() == 0) {
                newline();
            } else {
                final String formatted;
                if (!params.isEmpty()) {
                    formatted = new Template(params).resolve(parameters.length == 0 ? s : String.format(s, parameters));
                } else {
                    if (parameters.length == 0) {
                        formatted = s;
                    } else {
                        formatted = String.format(s, parameters);
                    }
                }
                if (formatted.contains("\n")) {
                    Arrays.stream(formatted.split("\r?\n", -1)).forEach(this::write);
                } else {
                    lines.add((tabStop > 0 ? String.format("%" + tabStop + "s", " ") : "") + formatted);
                }
            }
        }
        return this;
    }

    @Override
    public Segment newline() {
        lines.add("");
        return this;
    }

    @Override
    public Segment write_r(String s, Object... parameters) {
        write(s, parameters);
        tabStop += TAB;
        return this;
    }

    @Override
    public Segment write_l(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < openingTabStop) {
            tabStop = openingTabStop;
        }
        write(s, parameters);
        return this;
    }
}
