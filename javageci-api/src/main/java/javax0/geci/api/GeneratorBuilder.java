package javax0.geci.api;

/**
 * <p>The builder subclasses created by the {@link GeneratorBuilder} implement this interface. The fact that those
 * builders are instances of this interface can be used by auxiliary classes that help the registration of different
 * generators, like the {@code javax0.geci.docugen.Register} class. That way the {@code build()} method can be called on
 * the builders without knowing in the code the exact type.</p>
 */
public interface GeneratorBuilder {
    /**
     * The method that closes the builder, creates the builder and ...
     * @return the newly created builder
     */
    Generator build();
}
