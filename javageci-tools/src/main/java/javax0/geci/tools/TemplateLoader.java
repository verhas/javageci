package javax0.geci.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {
    private static final ClassLoader loader = TemplateLoader.class.getClassLoader();
    private static final String QUOTE = "```";

    /**
     * Get the content of a template either from the name of the Java
     * resource loading the resource or extracting it from the string in
     * case the argument string contains the template itself.
     *
     * @param template the name of the resource that contains the
     *                 template, or the template itself if is starts and
     *                 ends with three back-ticks (like code segment in
     *                 markdown). In this case the back-ticks are removed
     *                 from the returned string.
     * @return the content of the template. In case the template cannot
     * be loaded then a Java comment formatted error message is
     * returned. If the argument is {@code null} then the return value
     * is also {@code null}.
     */
    public static String getTemplateContent(String template) {
        if (template == null) {
            return null;
        }

        if (template.startsWith(QUOTE) && template.endsWith(QUOTE)) {
            return template.substring(3, template.length() - 3);
        }

        try {
            final var resource = loader.getResource(template);
            if (resource != null) {
                return new String(Files.readAllBytes(
                    Paths.get(fix(resource.getFile()))), StandardCharsets.UTF_8)
                        .replaceAll("\r\n", "\n");
            } else {
                return "/* template '" + template + "' was not loaded */";
            }
        } catch (IOException e) {
            try (final var sw = new StringWriter();
                 final var pw = new PrintWriter(sw)) {
                e.printStackTrace(pw);
                return "/* template '" + template + "' was not loaded : \n"
                        + sw.toString()
                        + "*/";
            } catch (IOException ioegnored) {
                return "/* template '" + template + "' was not loaded\n"
                        + "*/";
            }
        }
    }

    /**
     * Quote a string so that this will be interpreted by {@link
     * #getTemplateContent(String)} as the template itself and not the
     * name of te resource that contains the template.
     *
     * @param s the string that contains the template
     * @return the string preceeded and appended with three backticks.
     */
    public static String quote(String s) {
        return QUOTE + s + QUOTE;
    }

    /**
     * On Windows operating system the URI.getFile() returns a string
     * that looks like {@code /C:/...} which is not a valid file name.
     * The leading / character has to be removed
     *
     * @param s the file name to be fixed
     * @return the fixed file name that can already be opened and read
     */
    private static String fix(String s) {
        if (s != null && s.length() > 2 &&
                s.charAt(0) == '/' && s.charAt(2) == ':') {
            return s.substring(1);
        } else {
            return s;
        }
    }
}
