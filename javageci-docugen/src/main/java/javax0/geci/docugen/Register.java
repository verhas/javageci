package javax0.geci.docugen;

import javax0.geci.api.GeneratorBuilder;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.InvocationTargetException;

/**
 * Auxiliary class that helps registering the snippet generators.
 */
public class Register {

    public static Register register() {
        return new Register();
    }

    private boolean ordered = false;
    private String filePattern = null;

    public Register ordered() {
        ordered = true;
        return this;
    }

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

    public GeneratorBuilder[] generators(GeneratorBuilder... snippetBuilders) {
        for (int i = 0; i < snippetBuilders.length; i++) {
            try {
                if (ordered) {
                    GeciReflectionTools.invoke("phase").on(snippetBuilders[i]).types(int.class).args(i);
                }
                if (filePattern != null && !NonConfigurable.class.isAssignableFrom(snippetBuilders[i].getClass().getEnclosingClass())) {
                    GeciReflectionTools.invoke("files").on(snippetBuilders[i]).types(CharSequence.class).args(filePattern);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        return snippetBuilders;
    }

    public GeneratorBuilder[] allSnippetGenerators() {
        return generators(SnippetCollector.builder(),
            SnippetAppender.builder(),
            SnippetRegex.builder(),
            SnippetTrim.builder(),
            SnippetNumberer.builder(),
            SnipetLineSkipper.builder(),
            MarkdownCodeInserter.builder(),
            JavaDocSnippetInserter.builder());
    }

}
