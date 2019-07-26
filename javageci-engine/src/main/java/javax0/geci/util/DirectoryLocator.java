package javax0.geci.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contains the information that can be used to locate a certain
 * directory. This contains the array of alternative directories and a
 * predicate that can identify the directory that is the one to be used
 * from the array of directories.
 *
 * <p>Explanation:
 *
 * <p>When the Geci framework works it can be started fron different
 * directories. It may start from the root of the project but in case of
 * multi-module project it can start from the root of a module project
 * (sub-project). It depends on how the tests are started. Because we do
 * not know the current workign directory we cannot simply specify a
 * relative file name for the directory where the sources, the framework
 * works on, are. Using absolute file names is also out of question. It
 * would ruin portability.
 *
 * <p>The solution is that a source is specified as an array of
 * alternative directories. When we want to find the actual directory
 * then the search starts from the zero-th element of the array to the
 * last and checks if the predicate of the locator says that this is the
 * directory. The default predicate simply checks that the directory
 * exists, but in some cases this is not enough.
 *
 * <p>If the predicate matches a directory name but the directory cannot
 * be opened then the framework will throw an exception.
 */
public class DirectoryLocator implements javax0.geci.api.DirectoryLocator {
    private final String[] directories;

    final Predicate<String> predicate;

    public Stream<String> alternatives(){
        return Arrays.stream(directories);
    }

    /**
     * See {@link javax0.geci.api.DirectoryLocator#test(String)}.
     *
     * <p>The default predicate checks that there is an existing
     * directory with the given name.
     *
     * @param file See {@link javax0.geci.api.DirectoryLocator#test(String)}.
     * @return See {@link javax0.geci.api.DirectoryLocator#test(String)}.
     */
    @Override
    public boolean test(String file) {
        return predicate.test(file);
    }

    /**
     * Create a locator with the directories and the predicate.
     *
     * @param predicate   the predicate that selects the directory
     * @param directories the directories to be searched for the sources
     */
    public DirectoryLocator(Predicate<String> predicate, String[] directories) {
        this.directories = directories;
        this.predicate = predicate;
    }
}
