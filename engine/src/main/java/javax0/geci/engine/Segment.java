package javax0.geci.engine;

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
    public void close() {
        tabStop = openingTabStop;
    }

    @Override
    public void write(String s, Object... parameters) {
        if (s.trim().length() == 0) {
            newline();
        } else {
            var formatted = String.format(s, parameters);
            if (formatted.contains("\n")) {
                Arrays.stream(formatted.split("\n")).forEach(this::write);
            } else {
                lines.add(" ".repeat(tabStop) + formatted);
            }
        }
    }

    @Override
    public void newline() {
        lines.add("");
    }

    @Override
    public void write_r(String s, Object... parameters) {
        write(s, parameters);
        tabStop += TAB;
    }

    @Override
    public void write_l(String s, Object... parameters) {
        tabStop -= TAB;
        if (tabStop < openingTabStop) {
            tabStop = openingTabStop;
        }
        write(s, parameters);
    }
}
