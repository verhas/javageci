package javax0.geci.engine;

import java.util.LinkedList;
import java.util.List;

public class Segment implements javax0.geci.api.Segment {
    private static final int TAB = 4;
    final List<String> lines = new LinkedList<>();
    private int tabStop;
    final private int openingTabStop;

    public Segment(int tabStop) {
        this.openingTabStop = tabStop;
        this.tabStop = tabStop;
    }

    @Override
    public void close(){
        tabStop = openingTabStop;
    }

    @Override
    public void write(String s) {
        if (s.trim().length() == 0) {
            newline();
        } else {
            lines.add(" ".repeat(tabStop) + s);
        }
    }

    @Override
    public void newline() {
        lines.add("");
    }

    @Override
    public void write_r(String s) {
        write(s);
        tabStop += TAB;
    }

    @Override
    public void write_l(String s) {
        tabStop -= TAB;
        if (tabStop < openingTabStop) {
            tabStop = openingTabStop;
        }
        write(s);
    }
}
