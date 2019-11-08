package javax0.geci.engine;

import javax0.geci.api.Source;

/**
 * Source store can retrieve source objects. There are two implementations.
 * One uses the {@link FileCollector} to get the files. This is used during normal operation.
 * The other one returns the source objects that were previously added to it, so this is
 * a real store. This is used during test when sources are used in mock mode.
 */
interface SourceStore {

    /**
     * Get the source object associated with specific file name.
     *
     * @param fileName the name of the file from which the source is read
     * @return the source object for the file
     */
    Source get(String fileName);

    /**
     * Get the source object associated with specific file name from a set.
     * @param set the source set in which the source object should be
     * @param fileName the name of the file from which the source is read
     * @return the source object for the file from the given set
     */
    Source get(Source.Set set, String fileName);
}
