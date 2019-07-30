package javax0.geci.tools;

/**
 * Simplified and limited version of Levenshtein distance calculation.
 * For more information on this distance google Levenshtein.
 *
 * <p> This implementation is used to guess the parameter name in case
 * it is not found because the user made a typo. In that case the error
 * message suggest the closest keyword. Thus the calculation of the
 * distance is limited to {@code MAX_COST}. Because of this the
 * recursive and "slow" algorithm is used as it cannot explode
 * exponentially because the maximal cost is limited to {@code
 * MAX_COST}.
 */
class Levenshtein {

    private static final int MAX_COST = 5;

    static int distance(String x, String y) {
        final var lev = new Levenshtein();
        lev.maxCost = MAX_COST;
        return lev.calculate(x, y, 0);
    }

    private int maxCost;

    private int calculate(String x, String y, int cost) {
        if (cost >= maxCost) {
            return maxCost;
        }
        if (x.isEmpty()) {
            return y.length();
        }

        if (y.isEmpty()) {
            return x.length();
        }
        int thisCost = x.charAt(0) == y.charAt(0) ? 0 : 1;
        int substitution = calculate(x.substring(1), y.substring(1), cost + thisCost)
                + thisCost;
        int insertion = calculate(x, y.substring(1), cost + 1) + 1;
        final var min = substitution > insertion ? insertion : substitution;
        int deletion = calculate(x.substring(1), y, cost + 1) + 1;
        return min < deletion ? min : deletion;
    }
}
