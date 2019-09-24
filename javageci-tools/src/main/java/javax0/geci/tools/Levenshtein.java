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

    /**
     * <p>Calculate the distance between two strings. The distance between two strings is the number of character
     * changes that are needed to change one string to the other.</p>
     * <p>Examples</p>
     * <table><caption></caption>
     * <tr><th>String 1</th><th>String 2</th><th>Distance</th></tr>
     * <!-- snip Levenshtein_test regex="escape='~'
     * replace='|Assertions~.assertEquals~((~d+)~s*,~s*Levenshtein.distance~((\".*?\")~s*,~s*(\".*?\")~)~);|<tr><td>$2</td><td>$3</td><td>$1</td></tr>|'"
     * -->
     *         <tr><td>"same string"</td><td>"same string"</td><td>0</td></tr>
     *         <tr><td>"same"</td><td>"seme"</td><td>1</td></tr>
     *         <tr><td>"same"</td><td>"shame"</td><td>1</td></tr>
     *         <tr><td>"same"</td><td>"sam"</td><td>1</td></tr>
     *         <tr><td>"same"</td><td>"shama"</td><td>2</td></tr>
     *         <tr><td>"same"</td><td>"sham"</td><td>2</td></tr>
     *         <tr><td>"same"</td><td>"esam"</td><td>2</td></tr>
     *         <tr><td>"same"</td><td>"xmek"</td><td>3</td></tr>
     *         <tr><td>"same"</td><td>"xyzk"</td><td>4</td></tr>
     * <!-- end snip -->
     * </table>
     *
     * @param x one of the strings
     * @param y the other string
     * @return the distance or max 5
     */
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
        final var min = Math.min(substitution, insertion);
        int deletion = calculate(x.substring(1), y, cost + 1) + 1;
        return Math.min(min, deletion);
    }
}
