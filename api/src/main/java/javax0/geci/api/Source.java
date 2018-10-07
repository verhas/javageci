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

    /**
     * Get the name of the class that corresponds to this source. The class may not exist though.
     *
     * @return the class name that was calculated from the file name
     */
    String getKlassName();

    /**
     * Get the class that corresponds to this source. If the class does not exists, either because the
     * source file is not a Java source or because the file was not compiled or just for any reason then
     * the method returns {@code null}.
     *
     * @return the class object or {@code null}
     */
    Class<?> getKlass();
}
