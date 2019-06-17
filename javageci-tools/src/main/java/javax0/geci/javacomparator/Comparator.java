package javax0.geci.javacomparator;

import java.util.List;
import java.util.function.BiPredicate;

public class Comparator implements BiPredicate<List<String>, List<String>> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param strings  the first input argument
     * @param strings2 the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(List<String> strings, List<String> strings2) {
        return false;
    }
}
