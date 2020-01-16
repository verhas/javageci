package javax0.geci.lexeger;

import javax0.geci.tools.AbstractTestSource;

import java.util.Collections;
import java.util.List;

class TestSource extends AbstractTestSource {
    private List<String> lines;

    TestSource(List<String> lines) {
        this.lines = lines;
    }
    TestSource(String line) {
        this.lines = Collections.singletonList(line);
    }

    @Override
    public List<String> borrows() {
        return lines;
    }

    @Override
    public void returns(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return String.join("\n", lines);
    }
}
