package javax0.geci.api;

/**
 * <p>A generator implementing this interface declares that it does not
 * generate any source code, it keeps distance from the code only
 * reading it and it does not touch any code. Such a generator is an
 * auxiliary generator collecting information from the existing source
 * code and usually storing it in the {@link Geci#context() context} field
 * of the Geci object.</p>
 *
 * <p>The framework will not treat it as an error if there are only
 * Distant generators in a run. In other cases the fact that the
 * generators do not touch any source code throws an exception.</p>
 *
 * <p><b>Note:</b> touching means that the generator writes some code.
 * The actual writing may be skipped when the written code is the same
 * as the existing one but still the generator tried to do its work. If
 * none of the generators try to write (touch) any source code then what
 * is the point to execute them? This is usually a signal of
 * misconfiguration and thus such situation throws an exception.)</p>
 *
 * <p>Also, if a distant generator tries to touch any source code
 * the touching itself will throw an exception.</p>
 */
public interface Distant {
}
