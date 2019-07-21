package javax0.geci.api;

/**
 * A generator implementing this interface declares that it does not generate any source code, it keeps distance from
 * the code only reading it and it does not touch any code. Such a generator is an auxiliary generator collection
 * information from the existing source code and usually storing it in the context of the Geci object.
 *
 * <p> The framework will not treat it as an error if there are only Distant generators in a run. In other cases the
 * fact that the generators do not touch any source code throws an exception.
 *
 * <p><b>Note:</b> touching means that the generator writes some code. The actual writing may be aborted when the written code
 * is the same as the existing one but still the generator tried to do its work. If none of the generatores try to write
 * (touch) any source code then what is the point to execute them? This is usually a signal of misconfiguration and thus
 * throws an exception.)
 *
 * <p>Also, if a distant generator tries to touch any source the code the touching will throw exception.
 */
public interface Distant {
}
