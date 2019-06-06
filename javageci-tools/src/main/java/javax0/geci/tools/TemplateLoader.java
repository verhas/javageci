package javax0.geci.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {
    private static final ClassLoader loader = TemplateLoader.class.getClassLoader();

    public static String getTemplateContent(String template) {
        if (template == null) {
            return null;
        }

        try {
            final var resource = loader.getResource(template);
            if (resource != null) {
                return Files.readString(
                        Paths.get(fix(resource.getFile())))
                        .replaceAll("\r\n", "\n");
            } else {
                return "/* template '" + template + "' was not loaded */";
            }
        } catch (IOException e) {
            final var sw = new PrintWriter(new StringWriter());
            e.printStackTrace(sw);
            return "/* template '" + template + "' was not loaded : \n"
                    + sw.toString()
                    + "*/";
        }
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
