package javax0.geci.api;

import java.util.stream.Stream;

/**
 * A directory locator helps to find the directory for a given source
 * set. It can be queried with multiple directory names and it can tell
 * if a certain directory name is OK for the source set. Usually the
 * directory locator checks general things and nothing that is special
 * to a certain source set. The default locator simply checks that the
 * directory exists and is a directory. Other implementations may check
 * the existence of certain files in the directory.
 */
public interface DirectoryLocator {

    /**
     * Return the possible alternative directory names that can be used
     * as source directory. The actual implementation should be
     * optimized to use the advantages of streams. If the calculation of
     * the different directories is resource intensive then the stream
     * can defer the later directories after the directories sooner were
     * processed and casted out as not possible alternatives. When a
     * directory from the alternatives is accepted the rest of the
     * stream will not be fetched.
     *
     * @return tha array of possible alternative directories
     */
    Stream<String> alternatives();

    /**
     * Test that the predicate matches the given file name.
     *
     * @param file the name of the directory. The name has to be
     *             normalized (no Windows like \ nonsense) and it MUST
     *             have a trailing {@code /}.
     * @return {@code true} if the file name matches the predicate
     */
    boolean test(String file);
}
