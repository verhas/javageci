package javax0.geci.iterate;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.TemplateLoader;
import javax0.geci.tools.Tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>Iterate is a more modern version of the Repeated generator based
 * on the experience using repeated. The code is a full rewrite and not
 * compatible with repeated therefore it is a separate generator and not
 * a new version of Repeated. The generator Repeated remains in the code
 * base, though it is going to be deprecated.</p>
 *
 * <p>Iterate uses a templates and values assigned to the templates
 * to generate code. For example:</p>
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
public class Iterate extends AbstractJavaGenerator {
    private static class Config {
        // /* TEMPLATE
        private String templateLine = "\\s*/\\*\\s*TEMPLATE\\s*";
        // LOOP field,type=birth,Date|name,String|age,int
        private String loopLine = "\\s*LOOP\\s+(.*)";
        // EDITOR-FOLD-ID clientSetters
        private String editorFoldLine = "\\s*EDITOR-FOLD-ID\\s+(\\w[\\w\\d]*)\\s*";
        // */
        private String templateEndLine = "^\\s*\\*/\\s*$";
    }

    private static class Template {
        int startLine;
        private String editorFold;
        private StringBuilder text = new StringBuilder();
        private List<Map<String, String>> values;
    }

    private List<Template> collectTemplates(Source source, CompoundParams params) {
        final var templates = new ArrayList<Template>();
        final var local = localConfig(params);
        final var templateLine = Pattern.compile(local.templateLine);
        final var templateEndLine = Pattern.compile(local.templateEndLine);
        final var loopLine = Pattern.compile(local.loopLine);
        final var editorFoldLine = Pattern.compile(local.editorFoldLine);
        Template template = null;
        int lineIndex = 0;
        for (final var line : source.getLines()) {
            lineIndex++;
            if (template != null) {
                if (isLoopLine(source, loopLine, template, line))
                    continue;
                if (isEditorFoldIdLine(editorFoldLine, template, line))
                    continue;
                if (isTemplateEndLine(templates, templateEndLine, template, line)) {
                    template = null;
                    continue;
                }
                template.text.append(line).append("\n");
            } else {
                if (templateLine.matcher(line).matches()) {
                    template = new Template();
                    template.startLine = lineIndex;
                    continue;
                }
            }
        }
        return templates;
    }

    private boolean isTemplateEndLine(ArrayList<Template> templates, Pattern templateEndLine, Template template, String line) {
        if (templateEndLine.matcher(line).matches()) {
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

    private boolean isLoopLine(Source source, Pattern loopLine, Template template, String line) {
        final var loopMatcher = loopLine.matcher(line);
        if (loopMatcher.matches()) {
            collectValues(template, loopMatcher.group(1), source);
            return true;
        }
        return false;
    }

    private void collectValues(Template template, String loopString, Source source) {
        final var eqIndex = loopString.indexOf('=');
        if (eqIndex == -1) {
            return;
        }
        final var keysCsv = loopString.substring(0, eqIndex).trim();
        final var keys = keysCsv.split(",");
        final var valuesCsvs = loopString.substring(eqIndex + 1).trim().split("\\|");
        template.values = new ArrayList<>();
        for (final var valuesCsv : valuesCsvs) {
            final var values = valuesCsv.split(",");
            if (values.length != keys.length) {
                throw new GeciException("Template in " +
                                            source.getAbsoluteFile() +
                                            ":" + template.startLine +
                                            " has different number of keys and values as in\n" +
                                            "keys:'" + keysCsv + "' values:'" + valuesCsv);
            }
            final var map = new HashMap<String,String>();
            for( int i = 0  ; i < keys.length ; i ++ ){
                map.put(keys[i],values[i]);
            }
            template.values.add(map);
        }
    }


    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var templates = collectTemplates(source,global);
        for( final var template : templates ){
            try (final var segment = source.open(template.editorFold)) {
                if (segment == null) {
                    throw new GeciException("Segment " + template.editorFold + " does not exist");
                }
                for( final var map : template.values){
                    for( final var entry : map.entrySet()){
                        segment.param(entry.getKey(),entry.getValue());
                    }
                    segment.write(template.text.toString());
                }
            }
        }
    }


    //<editor-fold id="configBuilder" configurableMnemonic="repeated">
    private String configuredMnemonic = "repeated";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static Iterate.Builder builder() {
        return new Iterate().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "editorFoldLine",
        "loopLine",
        "templateEndLine",
        "templateLine",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder editorFoldLine(String editorFoldLine) {
            config.editorFoldLine = editorFoldLine;
            return this;
        }

        public Builder loopLine(String loopLine) {
            config.loopLine = loopLine;
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
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.editorFoldLine = params.get("editorFoldLine", config.editorFoldLine);
        local.loopLine = params.get("loopLine", config.loopLine);
        local.templateEndLine = params.get("templateEndLine", config.templateEndLine);
        local.templateLine = params.get("templateLine", config.templateLine);
        return local;
    }
    //</editor-fold>
}
