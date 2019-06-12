package javax0.geci.api;

import java.util.function.Supplier;

/**
 * Generators get source code information from the framework and
 * generate code. File writing and inserting the code to the existing
 * source code and mixing the manual and generated code is up to the
 * framework.
 */
@FunctionalInterface
public interface Generator {

    /**
     * Process the {@link Source} and presumably generate some code.
     *
     * @param source the object representing the source code that can
     *               also be altered by the generation during the
     *               processing. The {@code source} object is not the
     *               raw source code, rather it represents it and the
     *               generator should use the API provided by the class
     *               {@link Source} to access the text of the code as a
     *               series of lines as {@link String} objects as well
     *               as to write generated code back to the source code.
     *               The actual file manipulation will be handled by the
     *               framework.
     */
    void process(Source source);

    /**
     * Signal if a certain phase is needed by this generator.
     * <p>
     * When a generator needs  more than one phase, say {@code n
     * > 1} to execute then this method should return {@code true} for
     * {@code phase} values {@code 0...n-1} and {@code false} if {@code
     * phase} is larger than {@code n-1}. This is the simplest case.
     * (Also such a generator should implement the method {@link
     * #phases()} to define the number of phases it needs.
     * <p>
     * In a more complex situation some special generators may cooperate
     * with other generators. Say there are two generators {@code A} and
     * {@code B}. Generator {@code A} performs certain task in the first
     * phase (phase {@code 0}). It may be collecting information from
     * source code, resources etc. Then {@code B} is needed to do its
     * task. To ensure that the data collection performed by {@code A}
     * in the first phase, the generator {@code B} should be run in the
     * second phase (phase {@code 1}). After {@code B} is finished the
     * first generator, generator {@code A} can finish its work in the
     * third phase (phase {@code 2}).
     * <p>
     * In this situation the generator {@code A} should return {@code
     * true} from this method when {@code phase} is zero or two and
     * {@code B} should return {@code true} when {@code phase} is one.
     * That way
     * <pre>
     * phase       A.activeIn(phase)     B.activeIn(phase)
     *   0           true                 false
     *   1           false                true
     *   2           true                 false
     * </pre>
     * <p>
     * Other than the phasing there is no guarantee on the order of the
     * generators execution.
     * <p>
     * The default behaviour implemented in the interface is to be
     * active in the phase {@code 0} only.
     *
     * @param phase the current phase. This value can also be stored in
     *              the generator instance to remember the actual phase
     *              when {@link #process(Source)} is invoked the next time
     *              after {@code activeIn()} returned {@code true}.
     * @return {@code true} if the generator needs to be invoked in
     * this phase.
     */
    default boolean activeIn(int phase) {
        return phase == 0;
    }

    /**
     * Return the max number of phases this generator needs. Geci will
     * query this method once before it starts its work to determine the
     * maximum number of phases it has to call the generators.
     * <p>
     * Note that the number of phases returned from this method may not
     * be the actual number of phases the Geci will execute. If Geci is
     * configured to run other generators that need more phases then
     * Geci will execute more phases.
     * <p>
     * The default behaviour is to require only one phase.
     *
     * @return the maximum number of phases.
     * Note that when this value is {@code n} then the phase numbers
     * will be {@code 0...n-1}.
     */
    default int phases() {
        return 1;
    }

    /**
     * Generators wanting to use the context should implement this
     * method. The default implementation simply ignores the context.
     * The actual implementation should store the context in an instance
     * variable and it is also recommended to fetch the objects using
     * the keys the generator intends to use calling the {@link
     * Context#get(Object, Supplier)} method
     *
     * @param context to be injected and to be stored by the generator
     */
    default void context(Context context) {
    }

}
