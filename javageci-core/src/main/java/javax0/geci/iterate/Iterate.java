package javax0.geci.iterate;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.templated.Context;
import javax0.geci.templated.Triplet;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.Untabber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * <p>Iterate is a more modern version of the Repeated generator based
 * on the experience using repeated. The code is a full rewrite and not
 * compatible with repeated therefore it is a separate generator and not
 * a new version of Repeated. The generator Repeated remains in the code
 * base, though it is going to be deprecated.</p>
 *
 * <p>Iterate uses a templates and values assigned to the templates
 * in order to generate code. For example:</p>
 *
 * <pre>{@code
 *   /*TEMPLATE
 *   void set{{Field}}({{type}} x){
 *       this.{{field}} = x;
 *   }
 *
 *   LOOP field,type=birth,Date|name,String|age,int
 *   EDITOR-FOLD-ID clientSetters
 *
 *   * / <-- no space in real code
 *   //<editor-fold id="clientSetter">
 *   //</editor-fold>
 * }</pre>
 * Will create code and insert into the editor fold something like this:
 *
 * <pre>{@code
 *   void setBirth(Date x){
 *       this.birth = x;
 *   }
 *
 *   void setName(String x){
 *       this.name = x;
 *   }
 *
 *   void setAge(int x){
 *       this.age = x;
 *   }
 * }</pre>
 *
 * <p>The context {@code Field} is calculated with a {@code define()}
 * assigned to the name {@code clientSetter}.</p>
 */

@AnnotationBuilder
@javax0.geci.core.annotations.Iterate
public class Iterate extends AbstractJavaGenerator {
    private static final Pattern editorFold = Pattern.compile("\\s*//\\s*<\\s*editor-fold.*?\\sid\\s*=\\s*\"(.*?)\".*");

    private static class Config {
        // /* TEMPLATE
        private String templateLine = "\\s*(?:/\\*\\s*)?TEMPLATE\\s*";
        // LOOP field,type=birth,Date|name,String|age,int
        private String loopLine = "\\s*LOOP\\s+(.*)";
        // EDITOR-FOLD-ID clientSetters
        private String editorFoldLine = "\\s*EDITOR-FOLD-ID\\s+(\\w[\\w\\d]*)\\s*";
        // */
        private String templateEndLine = "\\s*\\*/\\s*";


        private String sep1 = ",";
        private String sep2 = "|";

        private String sep1Line = "\\s*SEP1\\s+([^\\s]*)\\s*";
        private String sep2Line = "\\s*SEP2\\s+([^\\s]*)\\s*";
        private String escapeLine = "\\s*(?://)?\\s*ESCAPE\\s*";
        private String skipLine = "\\s*(?://)?\\s*SKIP\\s*";
        private Consumer<Context> define = null;
    }

    private static class Template {
        private String sep1 = null;
        private String sep2 = null;
        private int startLine;
        private String editorFold = null;
        private List<String> lines = new ArrayList<>();
        private List<Map<String, String>> values;
    }

    private List<Template> collectTemplates(Source source, Config local) {
        final var templates = new ArrayList<Template>();
        final var templateLine = Pattern.compile(local.templateLine);
        final var templateEndLine = Pattern.compile(local.templateEndLine);
        final var loopLine = Pattern.compile(local.loopLine);
        final var editorFoldLine = Pattern.compile(local.editorFoldLine);
        final var sep1Line = Pattern.compile(local.sep1Line);
        final var sep2Line = Pattern.compile(local.sep2Line);
        final var escapeLine = Pattern.compile(local.escapeLine);
        final var skipLine = Pattern.compile(local.skipLine);
        Template template = null;
        Template danglet = null;
        int lineIndex = 0; // keep track of line numbers, used only in error messages
        boolean escape = false;
        boolean skip = false;
        for (final var line : source.getLines()) {
            lineIndex++;
            if (template != null) {
                if (skip) {
                    skip = false;
                    continue;
                }
                if (!escape) {
                    if (isLoopLine(source, loopLine, template, line, local))
                        continue;
                    if (isEditorFoldIdLine(editorFoldLine, template, line))
                        continue;
                    if (isTemplateEndLine(templates, templateEndLine, template, line)) {
                        danglet = getDanglingTemplate(template);
                        template = null;
                        continue;
                    }
                    if (isSep1(template, sep1Line, line)) {
                        continue;
                    }
                    if (isSep2(template, sep2Line, line)) {
                        continue;
                    }
                    if (escapeLine.matcher(line).matches()) {
                        escape = true;
                        continue;
                    }
                    if (skipLine.matcher(line).matches()) {
                        skip = true;
                        continue;
                    }
                }
                escape = false;
                template.lines.add(line);
            } else {
                if (templateLine.matcher(line).matches()) {
                    template = new Template();
                    template.startLine = lineIndex;
                    continue;
                }
                if (danglet != null) {
                    final var editorFoldMatcher = editorFold.matcher(line);
                    if (editorFoldMatcher.matches()) {
                        danglet.editorFold = editorFoldMatcher.group(1);
                        danglet = null;
                    }
                }
            }
        }
        return templates;
    }

    /**
     * A template is dangling during the template scanning process if
     * there is no editor fold assigned to it. In this case the next
     * editor fold will be assigned. The scanning process keeps track of
     * the last dangling template in the local variable {@code danglet}
     * and when there is a dangling template and the scanning process
     * meets a editor fold outside of a template then it will assign the
     * ID of that editor fold to the dangling template.
     *
     * @param template the template that we just finish processing
     * @return the template if it does not have an editor fold
     * identifier defined or {@code null}
     */
    private Template getDanglingTemplate(Template template) {
        Template danglet;
        if (template.editorFold == null) {
            danglet = template;
        } else {
            danglet = null;
        }
        return danglet;
    }

    /*
    TEMPLATE
    private boolean isSep{{n}}(Template template, Pattern sep{{n}}Line, String line) {
        final var sep{{n}}LineMatcher = sep{{n}}Line.matcher(line);
        if (sep{{n}}LineMatcher.matches()) {
            template.sep{{n}} = Pattern.quote(sep{{n}}LineMatcher.group(1));
            return true;
        }
        return false;
    }

    LOOP n=1|2
    */
    //<editor-fold id="isSep_n">
    private boolean isSep1(Template template, Pattern sep1Line, String line) {
        final var sep1LineMatcher = sep1Line.matcher(line);
        if (sep1LineMatcher.matches()) {
            template.sep1 = Pattern.quote(sep1LineMatcher.group(1));
            return true;
        }
        return false;
    }

    private boolean isSep2(Template template, Pattern sep2Line, String line) {
        final var sep2LineMatcher = sep2Line.matcher(line);
        if (sep2LineMatcher.matches()) {
            template.sep2 = Pattern.quote(sep2LineMatcher.group(1));
            return true;
        }
        return false;
    }

    //</editor-fold>

    private boolean isTemplateEndLine(ArrayList<Template> templates, Pattern templateEndLine, Template template, String line) {
        if (templateEndLine.matcher(line).matches()) {
            template.lines = Untabber.untab(template.lines);
            templates.add(template);
            return true;
        }
        return false;
    }

    private boolean isEditorFoldIdLine(Pattern editorFoldLine, Template template, String line) {
        final var editorFoldLineMatcher = editorFoldLine.matcher(line);
        if (editorFoldLineMatcher.matches()) {
            template.editorFold = editorFoldLineMatcher.group(1);
            return true;
        }
        return false;
    }

    /**
     * Checks that the current {@code line} is a {@code LOOP} command and in case it is then processes it and stores the
     * value in the template object.
     *
     * @param source   the source code object
     * @param loopLine the pattern that matches a loopLine
     * @param template the current template that the loop line belongs to
     * @param line     the current line
     * @param local    the local configuration
     * @return {@code true} in case the line was processed
     */
    private boolean isLoopLine(Source source, Pattern loopLine, Template template, String line, Config local) {
        final var loopMatcher = loopLine.matcher(line);
        if (loopMatcher.matches()) {
            collectValues(template, loopMatcher.group(1), source, local);
            return true;
        }
        return false;
    }

    /**
     * <p>Collect the values using the {@code loopString} as a definition. In the simplest case the `loopString` has the
     * format (example):</p>
     *
     * <pre>{@code
     * LOOP type,var=int,aInt|long,aLong|short,aShort
     * }</pre>
     *
     * <p>The values are added to the {@code template.values} list. In case this list does not exists it will be
     * initialized. SUbsequent calls for the same template will add the values.</p>
     *
     * @param template   the current template for which we collect the values
     * @param loopString the string that describes the loop variables and their values
     * @param source     the source code object
     * @param local      the local configuration
     */
    private void collectValues(Template template, String loopString, Source source, Config local) {
        final String sep1 = template.sep1 != null ? template.sep1 : Pattern.quote(local.sep1);
        final String sep2 = template.sep2 != null ? template.sep2 : Pattern.quote(local.sep2);
        final var eqIndex = loopString.indexOf('=');
        if (eqIndex == -1) {
            throw new GeciException("LOOP line cannot be parsed in template that starts at\n"
                + errorLocation(template, source));
        }
        final var keysCsv = loopString.substring(0, eqIndex).trim();
        final var keys = keysCsv.split(sep1);
        final var valuesCsvs = loopString.substring(eqIndex + 1).trim().split(sep2);
        if (template.values == null) {
            template.values = new ArrayList<>();
        }
        for (final var valuesCsv : valuesCsvs) {
            final var values = valuesCsv.split(sep1);
            if (values.length != keys.length) {
                throw new GeciException("Template has different number of keys and values as in\n"
                    + "keys:'" + keysCsv + "' values:'" + valuesCsv + "\n"
                    + errorLocation(template, source));
            }
            final var map = new HashMap<String, String>();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
            template.values.add(map);
        }
    }


    private static String errorLocation(Template template, Source source) {
        return source.getAbsoluteFile() + ":" + template.startLine;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var local = localConfig(global);
        final var templates = collectTemplates(source, local);
        final var ctx = new Triplet();
        for (final var template : templates) {
            if (template.editorFold == null) {
                throw new GeciException("Template staring on the line " +
                    source.getAbsoluteFile() +
                    ":" + template.startLine +
                    " does not have an editor fold specified.");
            }
            try (final var segment = source.open(template.editorFold)) {
                if (segment == null) {
                    throw new GeciException("Segment " + template.editorFold + " does not exist");
                }
                for (final var map : template.values) {
                    for (final var entry : map.entrySet()) {
                        segment.param(entry.getKey(), entry.getValue());
                    }
                    if (local.define != null) {
                        ctx.triplet(source, klass, segment);
                        local.define.accept(ctx);
                    }
                    for (final var s : template.lines) {
                        segment.write(s);
                    }
                }
            }
        }
    }


    //<editor-fold id="configBuilder" configurableMnemonic="iterate">
    private String configuredMnemonic = "iterate";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static Iterate.Builder builder() {
        return new Iterate().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "editorFoldLine",
        "escapeLine",
        "loopLine",
        "sep1",
        "sep1Line",
        "sep2",
        "sep2Line",
        "skipLine",
        "templateEndLine",
        "templateLine",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }

    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder define(java.util.function.Consumer<javax0.geci.templated.Context> define) {
            config.define = define;
            return this;
        }

        public Builder editorFoldLine(String editorFoldLine) {
            config.editorFoldLine = editorFoldLine;
            return this;
        }

        public Builder escapeLine(String escapeLine) {
            config.escapeLine = escapeLine;
            return this;
        }

        public Builder loopLine(String loopLine) {
            config.loopLine = loopLine;
            return this;
        }


        public Builder sep1(String sep1) {
            config.sep1 = sep1;
            return this;
        }

        public Builder sep1Line(String sep1Line) {
            config.sep1Line = sep1Line;
            return this;
        }

        public Builder sep2(String sep2) {
            config.sep2 = sep2;
            return this;
        }

        public Builder sep2Line(String sep2Line) {
            config.sep2Line = sep2Line;
            return this;
        }

        public Builder skipLine(String skipLine) {
            config.skipLine = skipLine;
            return this;
        }

        public Builder templateEndLine(String templateEndLine) {
            config.templateEndLine = templateEndLine;
            return this;
        }

        public Builder templateLine(String templateLine) {
            config.templateLine = templateLine;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Iterate build() {
            return Iterate.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.define = config.define;
        local.editorFoldLine = params.get("editorFoldLine", config.editorFoldLine);
        local.escapeLine = params.get("escapeLine", config.escapeLine);
        local.loopLine = params.get("loopLine", config.loopLine);
        local.sep1 = params.get("sep1", config.sep1);
        local.sep1Line = params.get("sep1Line", config.sep1Line);
        local.sep2 = params.get("sep2", config.sep2);
        local.sep2Line = params.get("sep2Line", config.sep2Line);
        local.skipLine = params.get("skipLine", config.skipLine);
        local.templateEndLine = params.get("templateEndLine", config.templateEndLine);
        local.templateLine = params.get("templateLine", config.templateLine);
        return local;
    }
    //</editor-fold>
}
