package javax0.geci.engine;

import java.util.LinkedList;
import java.util.List;

public class Segment implements javax0.geci.api.Segment {
    final List<String> lines = new LinkedList<>();
    int tabStop = 0;

    @Override
    public void write(String s) {
        lines.add(" ".repeat(tabStop) + s);
    }

    @Override
    public void write_r(String s) {
        write(s);
        tabStop += 4;
    }

    @Override
    public void write_l(String s) {
        write(s);
        tabStop -= 4;
        if (tabStop < 0) {
            tabStop = 0;
        }
    }
}
