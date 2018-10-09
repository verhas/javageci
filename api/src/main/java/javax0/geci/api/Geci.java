package javax0.geci.api;

public interface Geci {

    /**
     * Add a new directory to the list of source directories that Geci should process.
     *
     * @param directory list of directories that can be used as alternatives. When looking for files the first is used
     *                  at start, if that fails then the second and so on.
     * @return {@code this}
     */
    Geci source(String ...directory);

    /**
     * Register a generator instance.
     *
     * @param generatorArr the generators to register
     * @return {@code this}
     */
    Geci register(Generator ...generatorArr);

    /**
     * Run the code generation.
     * @return {@code false} if the code generation did not produce any output. It means that the code was
     *         already up to date. {@code true} when code was generated. When the code generation is executed
     *         as a unit test this return value can be asserted and compilation may fail in case code was changed.
     */
    boolean generate() throws Exception;

}
