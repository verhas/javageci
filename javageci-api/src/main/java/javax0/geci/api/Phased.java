package javax0.geci.api;

/**
 * Generators that need to work in several phases should implement this
 * interface. If a generator does not implement this interface then the
 * generation will invoke it only in the phase zero that always happens.
 */
public interface Phased {
    /**
     * Signal if a certain phase is needed by this generator.
     *
     * @param phase the current phase
     * @return {@code true} if the generator needs to be invoked in
     *         this phase.
     */
    boolean active(int phase);
}
