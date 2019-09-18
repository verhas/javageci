package javax0.geci.docugen;

import javax0.geci.api.GeneratorBuilder;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * <p>Auxiliary class that helps registering the snippet generators.</p>
 *
 * <p>Snippet generators are many times are registered to be in consecutive phases. That way the registration
 * call invokes the builder and every time it invokes the {@code .phase(counter++)} call with some local {@code counter}
 * variable. Also when one snippet generator is configured to work on certain files then all the others are likely to
 * work on the same file types. To eliminate this repetition this class provides some auxiliary methods.</p>
 *
 * <p>The aim of these methods is to provide an array of {@link GeneratorBuilder} objects, which are already configured
 * and can be passed to the method {@link javax0.geci.api.Geci#register(GeneratorBuilder...)}.</p>
 *
 */
public class Register {

    /**
     * A static method that start the building up of the chained array build up.
     *
     * @return a new object that can be used to chain the further calls
     */
    public static Register register() {
        return new Register();
    }

    private boolean ordered = false;
    private String filePattern = null;

    /**
     * Tell the class that the snippet generators have to be ordered. If this method was called then each of the
     * generator builders will be called using the {@link AbstractSnippeter.Builder#phase(int)} method with an ever
     * increasing counter so that they will be invoked by the framework in the order they were given.
     *
     * @return {@code this}
     */
    public Register ordered() {
        ordered = true;
        return this;
    }

    /**
     * <p>This method can be used to specify the file extensions that the generators should work on. The generator
     * builder method {@link AbstractSnippeter.Builder#files(CharSequence)} specify a regular expression. This method
     * accepts the file extensions and converts it to a regular expression that will be passed to the {@code files()}
     * method.</p>
     *
     * <p>The convertsion takes each argument string {@code ext}, converts it to {@code \\.ext$} and joins these with
     * the regular expression OR-ing character: {@code |}.</p>
     *
     * @param extensions the array of file name extensions that the snippeters will work on
     * @return {@code this}
     */
    public Register fileExtensions(final String... extensions) {
        final var sb = new StringBuilder();
        var sep = "";
        for (final var s : extensions) {
            sb.append(sep).append("\\.").append(s).append("$");
            sep = "|";
        }
        filePattern = sb.toString();
        return this;
    }

    /**
     * Returns the array of generator builders calling {@code phase(int)} and {@code files("")} on them if needed and
     * possible. Use this method if you want to register only a subset of the snippeters and not all.
     *
     * @param snippetBuilders the snippet builders that are to be registered.
     * @return the prepared generator builders
     */
    public GeneratorBuilder[] generators(GeneratorBuilder... snippetBuilders) {
        int phase = 0;
        for (GeneratorBuilder snippetBuilder : snippetBuilders) {
            if (snippetBuilder instanceof AbstractSnippeter.Builder) {
                final var abstractBuilder = (AbstractSnippeter.Builder) snippetBuilder;
                if (ordered) {
                    abstractBuilder.phase(phase++);
                }
                if (filePattern != null && !NonConfigurable.class.isAssignableFrom(abstractBuilder.getClass().getEnclosingClass())) {
                    abstractBuilder.files(filePattern);
                }
            }
        }
        return snippetBuilders;
    }

    /**
     * Same as {@link #generators(GeneratorBuilder...)} but the arguments are not the builders, but the method reference
     * to the static method that returns the builder.
     *
     * @param snippetBuildersSuppliers is the array of suppliers
     * @return the prepared generator builders
     */
    public GeneratorBuilder[] generators(Supplier<GeneratorBuilder>... snippetBuildersSuppliers) {
        return generators(Arrays.stream(snippetBuildersSuppliers).map(Supplier::get).toArray(GeneratorBuilder[]::new));
    }

    /**
     * <p>Returns the array of generator builders calling {@code phase(int)} and {@code files("")} on them if needed and
     * possible. The method will use all the generators in the usual order.</p>
     *
     * <p>The generators for which the builders are used in the order as they will be in the output array:</p>
     *
     * <ol>
     * <!-- snip List_of_All_Generators_in_Register regex="replace='|::builder,?|</li>|' replace='|^~s*|<li>|' escape='~'"-->
     * <li>SnippetCollector</li>
     * <li>SnippetAppender</li>
     * <li>SnippetRegex</li>
     * <li>SnippetTrim</li>
     * <li>SnippetNumberer</li>
     * <li>SnipetLineSkipper</li>
     * <li>MarkdownCodeInserter</li>
     * <li>JavaDocSnippetInserter</li>
     * <!-- end snip -->
     * </ol>
     *
     *
     * @return the prepared generator builders
     */
    public GeneratorBuilder[] allSnippetGenerators() {
        return generators(
            // snippet List_of_All_Generators_in_Register
            SnippetCollector::builder,
            SnippetAppender::builder,
            SnippetRegex::builder,
            SnippetTrim::builder,
            SnippetNumberer::builder,
            SnipetLineSkipper::builder,
            MarkdownCodeInserter::builder,
            JavaDocSnippetInserter::builder
            //end snippet
        );
    }

}
