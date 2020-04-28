package javax0.geci.repeated;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.templated.Context;
import javax0.geci.templated.Triplet;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.TemplateLoader;
import javax0.geci.tools.Tracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AnnotationBuilder
public class Repeated extends AbstractJavaGenerator {
    private static class Config {
        private String start = ".*//\\s*START\\s*";
        private String matchLine = "(.*)";
        private String end = ".*//\\s*END\\s*";
        private String templateStart = "\\s*/\\*\\s*TEMPLATE\\s+(\\w*)\\s*";
        private String templateEnd = "\\s*\\*/\\s*";
        private String values = null;
        private Function<Class, List<String>> valuesSupplier = null;
        private CharSequence selector = "";
        private final CharSequence template = "";
        private Context ctx = new Triplet();
        private final Map<String, String> templatesMap = new HashMap<>();
        private BiFunction<Context, String, String> resolver;
        private final Map<String, BiFunction<Context, String, String>> resolverMap = new HashMap<>();
        private BiConsumer<Context, String> define;
        private final Map<String, BiConsumer<Context, String>> defineMap = new HashMap<>();

        private void setTemplate(CharSequence template) {
            if (templatesMap.containsKey(selector.toString())) {
                throw new GeciException("Selector '" + selector + "' already has a template");
            }
            templatesMap.put(selector.toString(), template.toString());
        }

        private void setResolver(BiFunction<Context, String, String> resolver) {
            if (resolverMap.containsKey(selector.toString())) {
                throw new GeciException("Selector '" + selector + "' already has a resolver");
            }
            resolverMap.put(selector.toString(), resolver);
        }

        private void setDefine(BiConsumer<Context, String> define) {
            if (defineMap.containsKey(selector.toString())) {
                throw new GeciException("Selector '" + selector + "' already has a define");
            }
            defineMap.put(selector.toString(), define);
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var local = localConfig(global);
        final var startPattern = Pattern.compile(local.start);
        final var matchLinePattern = Pattern.compile(local.matchLine);
        final var endPattern = Pattern.compile(local.end);
        final var templateStartPattern = Pattern.compile(local.templateStart);
        final var templateEndPattern = Pattern.compile(local.templateEnd);
        try (final var pos = Tracer.push("Config", null)) {
            Tracer.log("startPattern", "" + startPattern);
            Tracer.log("matchLinePattern", "" + matchLinePattern);
            Tracer.log("endPattern", "" + endPattern);
            Tracer.log("templateStartPattern", "" + templateStartPattern);
            Tracer.log("templateEndPattern", "" + templateEndPattern);
        }
        boolean switchOn = false;
        boolean templateOn = false;
        final var loopVars = new ArrayList<String>();
        final var parsed = new StringBuilder();
        var selector = "";
        int templateTabbing = 0;
        try (final var x = Tracer.push("TemplateSearch", "searching...")) {
            for (final var line : source.getLines()) {
                if (templateOn) {
                    Tracer.push("TemplateLine", line.substring(line.length() > templateTabbing ? templateTabbing : 0));
                } else if (switchOn) {
                    Tracer.push("ValueLine", line.substring(line.length() > templateTabbing ? templateTabbing : 0));
                } else {
                    Tracer.push("Line", line);
                }
                if (!templateOn && !switchOn) {
                    if (startPattern.matcher(line).matches()) {
                        Tracer.prepend("[START VALUES]");
                        switchOn = true;
                        continue;
                    }
                    final var templateStartMatcher = templateStartPattern.matcher(line);
                    if (templateStartMatcher.matches()) {
                        Tracer.prepend("[START TEMPLATE]");
                        templateOn = true;
                        selector = templateStartMatcher.group(1);
                        templateTabbing = countSpacesAtStart(line);
                        Tracer.log("TAB=" + templateTabbing + " SELECTOR=" + selector);
                        continue;
                    }
                }

                if (switchOn && endPattern.matcher(line).matches()) {
                    Tracer.prepend("[STOP]");
                    switchOn = false;
                    Tracer.pop();
                    Tracer.pop();
                    continue;
                }

                if (!switchOn && templateOn && templateEndPattern.matcher(line).matches()) {
                    Tracer.prepend("[STOP]");
                    templateOn = false;
                    deleteTrailingNewLine(parsed);
                    config.templatesMap.put(selector, TemplateLoader.quote(parsed.toString()));
                    parsed.delete(0, parsed.length());
                    Tracer.pop();
                    Tracer.pop();
                    continue;
                }

                if (templateOn) {
                    if (line.length() > templateTabbing) {
                        parsed.append(line.substring(templateTabbing));
                    }
                    parsed.append("\n");
                    Tracer.pop();
                    continue;
                }

                if (switchOn) {
                    final Matcher matcher = matchLinePattern.matcher(line);
                    if (matcher.find()) {
                        Tracer.log("Line contains a value");
                        if (matcher.groupCount() != 1) {
                            Tracer.log("Pattern did not return the value... GECI EXCEPTION is thrown");
                            throw new GeciException("matchLine does not contain any group between ( and )");
                        }
                        final var value = matcher.group(1);
                        Tracer.log("value=" + value);
                        loopVars.add(value);
                    } else {
                        Tracer.log("There is no value on the line");
                    }
                }
                Tracer.pop();
            }
        }
        if (local.values != null) {
            Tracer.log("Adding configured values [" + local.values + "]");
            loopVars.addAll(Arrays.asList(local.values.split(",")));
        }
        if (local.valuesSupplier != null) {
            Tracer.log("Adding values from configured values supplier");
            loopVars.addAll(local.valuesSupplier.apply(klass));
        }
        Tracer.log("final list of values: [" + String.join(",", loopVars) + "]");
        for (final var key : config.templatesMap.keySet()) {
            try (final var pos = Tracer.push("Segment", key)) {
                final String segmentKey;
                if (key.isEmpty()) {
                    Tracer.log("This is the ID segment named '" + global.id() + "'");
                    segmentKey = global.id();
                } else {
                    segmentKey = key;
                }
                try(final var segment = source.open(segmentKey)) {
                    if (segment == null) {
                        throw new GeciException("Segment " + segmentKey + " does not exist");
                    } else {
                        final var template = config.templatesMap.get(key);
                        if (template != null) {
                            Tracer.log("Template", null, template);
                            config.ctx.triplet(source, klass, segment);
                            final var resolver = config.resolverMap.get(key);
                            final var define = config.defineMap.get(key);
                            final var templateContent = TemplateLoader.getTemplateContent(template);
                            Tracer.log("TemplateContent", null, templateContent);
                            final var resolvedTemplate = resolver == null ? templateContent : resolver.apply(config.ctx, templateContent);
                            for (final var loopVar : loopVars) {
                                segment.param("value", loopVar);
                                if (define != null) {
                                    define.accept(config.ctx, loopVar);
                                }
                                try (final var segmentParamsPos = Tracer.push("SegmentParams", null)) {
                                    segment.traceParams();
                                    segment.write(resolvedTemplate);
                                }
                            }
                            segment.traceLines();
                        } else {
                            Tracer.log("Template " + key + " does not exist");
                        }
                    }
                }
            }
        }
    }

    private void deleteTrailingNewLine(StringBuilder parsed) {
        if (parsed.length() > 0 && parsed.charAt(parsed.length() - 1) == '\n') {
            parsed.deleteCharAt(parsed.length() - 1);
            if (parsed.length() > 0 && parsed.charAt(parsed.length() - 1) == '\r') {
                parsed.deleteCharAt(parsed.length() - 1);
            }
        }
    }

    private static int countSpacesAtStart(String line) {
        int i = 0;
        while (i < line.length()) {
            if (line.charAt(i) != ' ') {
                return i;
            }
            i++;
        }
        return 0;
    }

    //<editor-fold id="configBuilder" configurableMnemonic="repeated">
    private String configuredMnemonic = "repeated";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static Repeated.Builder builder() {
        return new Repeated().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "end",
        "matchLine",
        "start",
        "templateEnd",
        "templateStart",
        "values",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder ctx(javax0.geci.templated.Context ctx) {
            config.ctx = ctx;
            return this;
        }

        public Builder define(java.util.function.BiConsumer<javax0.geci.templated.Context,String> define) {
            config.setDefine(define);
            return this;
        }

        public Builder end(String end) {
            config.end = end;
            return this;
        }

        public Builder matchLine(String matchLine) {
            config.matchLine = matchLine;
            return this;
        }

        public Builder resolver(java.util.function.BiFunction<javax0.geci.templated.Context,String,String> resolver) {
            config.setResolver(resolver);
            return this;
        }

        public Builder selector(CharSequence selector) {
            config.selector = selector;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder template(CharSequence template) {
            config.setTemplate(template);
            return this;
        }

        public Builder templateEnd(String templateEnd) {
            config.templateEnd = templateEnd;
            return this;
        }

        public Builder templateStart(String templateStart) {
            config.templateStart = templateStart;
            return this;
        }

        public Builder values(String values) {
            config.values = values;
            return this;
        }

        public Builder valuesSupplier(java.util.function.Function<Class,java.util.List<String>> valuesSupplier) {
            config.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Repeated build() {
            return Repeated.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.ctx = config.ctx;
        local.setDefine(config.define);
        local.end = params.get("end", config.end);
        local.matchLine = params.get("matchLine", config.matchLine);
        local.setResolver(config.resolver);
        local.selector = config.selector;
        local.start = params.get("start", config.start);
        local.setTemplate(config.template);
        local.templateEnd = params.get("templateEnd", config.templateEnd);
        local.templateStart = params.get("templateStart", config.templateStart);
        local.values = params.get("values", config.values);
        local.valuesSupplier = config.valuesSupplier;
        return local;
    }
    //</editor-fold>
}
