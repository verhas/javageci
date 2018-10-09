package javax0.geci.api;

import java.io.IOException;

public interface Source {
    /**
     * Return the named segment that the generator can write. Return {@code null} if there is no such segment
     * in the file.
     *
     * @param id the name of the segment as defined in the {@code id="..."} xml tag of the {@code <editor-fold ...>}
     * @return the segment or {@code null}.
     * @throws IOException in case there is no file or file is not readable
     */
    Segment open(String id) throws IOException;

    /**
     * Open the "global" segment that is the whole source file. Usually used on sources that are created calling
     * {@link #newSource(String)}
     *
     * @return the new segment object.
     * @throws IOException in case there is no file or file is not readable
     */
    Segment open() throws IOException;

    /**
     * Create a new source that may or may not exist. This source is going to be generated and if it already existed
     * it will be overwritten.
     * @param fileName relative file name to the current source.
     *
     * @return the new {@code Source} object.
     * @throws IOException in case there is no file or file is not readable
     */
    Source newSource(String fileName) throws IOException;

    /**
     * Initialize a segment. This is needed in case it is possible that the code generator does not
     * write anything into the segment. In that case the segment may not even be opened and in that
     * case the segment is not touched and the old, presumably garbage may be there. For exmaple
     * you have a setter/getter generated code that selects some of the fields only, say only the
     * 'protected' onces. During the development you change the last 'protected' field to private
     * and there remains no field for which setter and getter is to be generated. If the segment is
     * not init-ed, then the code generator would not touch the segment and it may contain the
     * setter and the getter for the last 'protected' by now 'private' field.
     * <p>
     * Technically calling init is similar to opening a segment, though init should be more lenient
     * to opening non-existent segments.
     *
     *
     * @param id the identifier of the segment
     * @throws IOException in case there is no file or file is not readable
     */
    void init(String id) throws IOException;

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
