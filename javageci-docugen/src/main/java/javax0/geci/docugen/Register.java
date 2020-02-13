package javax0.geci.docugen;

import javax0.geci.api.GeneratorBuilder;
import javax0.geci.tools.ArrayTools;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    private int phase = 0;
    private GeneratorBuilder[] builders = null;

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
     * <p>The conversion takes each argument string {@code ext}, converts it to {@code \\.ext$} and joins these with
     * the regular expression OR-ing character: {@code |}.</p>
     *
     * @param extensions the array of file name extensions that the snippeters will work on, or {@code null} not to
     *                   register extensions.
     * @return {@code this}
     */
    public Register fileExtensions(final String... extensions) {
        filePattern = Arrays.stream(extensions).map(s -> "\\." + s + "$").collect(Collectors.joining("|"));
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
     * <p>Returns the array of generator builders calling {@code phase(int)} and {@code files("")} on them if needed and
     * possible. The method will use all the generators in the usual order.</p>
     *
     * <p>The generators for which the builders are used in the order as they will be in the output array:</p>
     * <p>
     * <!--
     * This snip pulls all the generators that are listed in this method in the code (the snippet is inside the method)
     * removes the '::builder,' part from the end of the line replacing it with </li> and then adds an <li> tag at the
     * start. This is a simple example how you can use snippet handling to document the code using the code.
     * -->
     * <ol>
     * <!-- snip List_of_All_Generators_in_Register regex="replace='|.builder~(~),?.*|</li>|' replace='|^~s*|<li>|' escape='~'"-->
     * <li>SnippetCollector</li>
     * <li>SnippetAppender</li>
     * <li>SnippetTrim</li>
     * <li>SnippetNumberer</li>
     * <li>SnippetRegex</li>
     * <li>SnipetLineSkipper</li>
     * <li>SnippetNumberer</li>
     * <li>MarkdownCodeInserter</li>
     * <li>JavaDocSnippetInserter</li>
     * <!-- end snip -->
     * </ol>
     * Note that the generator {@link SnippetNumberer} is registered twice. Once it is registered before the line
     * skipping and regular expression handling (both can remove lines from snippets) and once after it. The first
     * instance is registered with an altered mnemonic and it is named "{@code prenumber}". This can be used if we want
     * to have the line numbering show that some lines were deleted from the snippet.
     *
     * @return the prepared generator builders
     */
    public GeneratorBuilder[] allSnippetGenerators() {
        builders = ArrayTools.join(builders, generators(
            // snippet List_of_All_Generators_in_Register
            SnippetCollector.builder(),
            SnippetAppender.builder(),
            SnippetTrim.builder(),
            SnippetNumberer.builder().mnemonic("prenumber"),
            SnippetRegex.builder(),
            SnipetLineSkipper.builder(),
            SnippetNumberer.builder(),
            MarkdownCodeInserter.builder(),
            JavaDocSnippetInserter.builder()
            //end snippet
        ));
        return builders;
    }

    /**
     * <p>Adds all the generators to the array of generators and returns. Using this method is same as {@link
     * #allSnippetGenerators()} except it returns {@code this} and it is possible to call {@link
     * #add(GeneratorBuilder...)} afterwards AND you MUST call at the end {@link #get()} to get the final list of the
     * generators.</p>
     *
     * <p>You have to use this method when you want to register all the snippet handling generators and in addition to
     * that you want to register other generators as well. The typical example when you want to register an instance of
     * {@link SnippetNumberer} generator in the predefined order but also another with a different mnemonic that runs
     * after all the other generators. That way a snippet can be numbered before and also after killing lines.</p>
     *
     * @return {@code this}
     */
    public Register allSnippetGeneratorsAnd() {
        allSnippetGenerators();
        return this;
    }

    public Register add(GeneratorBuilder... newBuilders) {
        builders = ArrayTools.join(builders, generators(newBuilders));
        return this;
    }

    public GeneratorBuilder[] get() {
        return builders;
    }

    public static GeneratorBuilder[] allSnippetHandlers() {
        return Register.register().ordered().fileExtensions("java","md","adoc").allSnippetGenerators();
    }

}
