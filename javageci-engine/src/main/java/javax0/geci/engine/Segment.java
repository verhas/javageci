package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.tools.Template;

import java.util.*;

public class Segment implements javax0.geci.api.Segment {
    private static final int TAB = 4;
    final List<String> preface = new LinkedList<>();
    final List<String> lines = new LinkedList<>();
    final List<String> postface = new LinkedList<>();
    final private int openingTabStop;
    private int tabStop;
    private final Map<String, String> params = new HashMap<>();

    public Segment(int tabStop) {
        this.openingTabStop = tabStop;
        this.tabStop = tabStop;
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
    public Set<String> paramKeySet() {
        return params.keySet();
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
            if (s.trim().length() == 0) {
                newline();
            } else {
                final String formatted;
                if (!params.isEmpty()) {
                    formatted = new Template(params).resolve(String.format(s, parameters));
                } else {
                    formatted = String.format(s, parameters);
                }
                if (formatted.contains("\n")) {
                    Arrays.stream(formatted.split("\r?\n")).forEach(this::write);
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
