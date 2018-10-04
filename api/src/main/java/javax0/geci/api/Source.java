package javax0.geci.api;

import java.io.IOException;

public interface Source {
    /**
     * Return the named segment that the generator can write. Return {@code null} if there is no such segment
     * in the file.
     *
     * @param id the name of the segment as defined in the {@code id="..."} xml tag of the {@code <editor-fold ...>}
     * @return the segment or {@code null}.
     * @throws IOException
     */
    Segment open(String id) throws IOException;

    String file();
}
