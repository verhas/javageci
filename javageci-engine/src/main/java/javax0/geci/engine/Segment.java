package javax0.geci.engine;

import javax0.geci.api.GeciException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Segment implements javax0.geci.api.Segment {
    private static final int TAB = 4;
    final List<String> lines = new LinkedList<>();
    final private int openingTabStop;
    private int tabStop;

    public Segment(int tabStop) {
        this.openingTabStop = tabStop;
        this.tabStop = tabStop;
    }

    @Override
    public String getContent() {
        return String.join("\n", lines);
    }

    @Override
    public void setContent(String content) {
        lines.clear();
        lines.addAll(Arrays.asList(content.split("\n", -1)));
    }

    @Override
    public void close() {
        tabStop = openingTabStop;
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
        if (s.trim().length() == 0) {
            newline();
        } else {
            var formatted = String.format(s, parameters);
            if (formatted.contains("\n")) {
                Arrays.stream(formatted.split("\n")).forEach(this::write);
            } else {
                lines.add((tabStop > 0 ? String.format("%" + tabStop + "s", " ") : "") + formatted);
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
