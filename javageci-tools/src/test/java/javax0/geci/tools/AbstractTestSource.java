package javax0.geci.tools;

import javax0.geci.api.Logger;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.List;

public abstract class AbstractTestSource implements Source {
    @Override
    public Segment open(String id) {
        return null;
    }

    @Override
    public void allowDefaultSegment() {
    }

    @Override
    public Segment safeOpen(String id) {
        return null;
    }

    @Override
    public Segment temporary() {
        return null;
    }

    @Override
    public Segment open() {
        return null;
    }

    /**
     * Get all the segment names that are defined in the source
     *
     * @return the set of the names of the segments
     */
    @Override
    public java.util.Set<String> segmentNames() {
        return null;
    }

    @Override
    public List<String> getLines() {
        return null;
    }

    @Override
    public List<String> borrows() {
        return null;
    }

     @Override
    public void returns(List<String> lines) {

    }

    @Override
    public String getAbsoluteFile() {
        return null;
    }

    @Override
    public Source newSource(String fileName) {
        return null;
    }

    @Override
    public Source newSource(Set set, String fileName) {
        return null;
    }

    @Override
    public void init(String id) {

    }

    @Override
    public String getKlassName() {
        return null;
    }

    @Override
    public String getKlassSimpleName() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public Class<?> getKlass() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }
}
