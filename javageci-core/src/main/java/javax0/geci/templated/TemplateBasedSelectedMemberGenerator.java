package javax0.geci.templated;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.TemplateLoader;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateBasedSelectedMemberGenerator extends AbstractJavaGenerator {

    private class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String fieldFilter = "true";
        private String methodFilter = "true";
        private boolean declaredOnly = true;
        private String selector = "";
        private final Map<String, Templates> templatesMap = new HashMap<>();
        private String preprocess = null;
        private String processField = null;
        private String processMethod = null;
        private String processFields = null;
        private String processMethods = null;
        private String postprocess = null;

        private Templates templates() {
            if (!templatesMap.containsKey(selector)) {
                templatesMap.put(selector, new Templates());
            }
            return templatesMap.get(selector);
        }

        private final void setPreprocess(String template) {
            templates().preprocess = template;
        }

        private final void setProcessField(String template) {
            templates().processField = template;
        }

        private final void setProcessFields(String template) {
            templates().processFields = template;
        }

        private final void setProcessMethod(String template) {
            templates().processMethod = template;
        }

        private final void setProcessMethods(String template) {
            templates().processMethods = template;
        }

        private final void setPostprocess(String template) {
            templates().postprocess = template;
        }

    }

    private static final Templates empty = new Templates();

    private static class Templates {
        private String preprocess = null;
        private String processField = null;
        private String processFields = null;
        private String processMethod = null;
        private String processMethods = null;
        private String postprocess = null;
    }

    @Override
    public final void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var fields = config.declaredOnly ? GeciReflectionTools.getDeclaredFieldsSorted(klass) : GeciReflectionTools.getAllFieldsSorted(klass);
        final var methods = config.declaredOnly ? GeciReflectionTools.getDeclaredMethodsSorted(klass) : GeciReflectionTools.getAllMethodsSorted(klass);
        var segment = source.open(global.id());
        preprocess(source, klass, global, segment);
        final var selectedFields = new ArrayList<Field>();
        for (final var field : fields) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(field, mnemonic()), global);
            var local = localConfig(params);
            if (Selector.compile(local.fieldFilter).match(field)) {
                selectedFields.add(field);
                process(source, klass, params, field, segment);
            }
        }
        final var selectedMethods = new ArrayList<Method>();
        for (final var method : methods) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(method, mnemonic()), global);
            var local = localConfig(params);
            if (Selector.compile(local.methodFilter).match(method)) {
                selectedMethods.add(method);
                process(source, klass, params, method, segment);
            }
        }
        processFields(source, klass, global, selectedFields, segment);
        processMethods(source, klass, global, selectedMethods, segment);
        postprocess(source, klass, global, segment);
    }

    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "classSimpleName", klass.getSimpleName(),
            "className", klass.getName(),
            "classCanonicalName", klass.getCanonicalName(),
            "classPackage", klass.getPackageName(),
            "classTypeName", klass.getTypeName(),
            "classGenericString", klass.toGenericString()
        );
        for (final var key : global.keySet()) {
            segment.param(key, global.get(key));
            segment.param("global." + key, global.get(key));
        }
        segment.write(getTemplateContent(local.preprocess, templates(local).preprocess));
    }

    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) throws Exception {
        final var local = localConfig(params);
        final var fieldType = field.getType();
        segment.param(
            "field.name", field.getName(),
            "field.genericString", field.toGenericString(),
            "field.classSimpleName", fieldType.getSimpleName(),
            "field.className", fieldType.getName(),
            "field.classCanonicalName", fieldType.getCanonicalName(),
            "field.classPackage", fieldType.getPackageName(),
            "field.classTypeName", fieldType.getTypeName(),
            "field.classGenericString", fieldType.toGenericString()
        );
        for (final var key : params.keySet()) {
            segment.param(key, params.get(key));
        }
        segment.write(getTemplateContent(local.processField, templates(local).processField));
        for (final var key : segment.paramKeySet()) {
            if (key.startsWith("field.")) {
                segment.param(key, null);
            }
        }
    }

    public void process(Source source, Class<?> klass, CompoundParams params, Method method, Segment segment) throws Exception {
        final var local = localConfig(params);
        final var returnType = method.getReturnType();
        segment.param(
            "method.name", method.getName(),
            "method.genericString", method.toGenericString(),
            "method.returnClassSimpleName", returnType.getSimpleName(),
            "method.returnClassName", returnType.getName(),
            "method.returnClassCanonicalName", returnType.getCanonicalName(),
            "method.returnClassPackage", returnType.getPackageName(),
            "method.returnClassTypeName", returnType.getTypeName(),
            "method.returnClassGenericString", returnType.toGenericString()
        );
        for (final var key : params.keySet()) {
            segment.param(key, params.get(key));
        }
        segment.write(getTemplateContent(local.processMethod, templates(local).processMethod));
        for (final var key : segment.paramKeySet()) {
            if (key.startsWith("method.")) {
                segment.param(key, null);
            }
        }
    }

    private String getTemplateContent(String localTemplateName, String globalTemplateName) {
        if (localTemplateName != null) {
            return TemplateLoader.getTemplateContent(localTemplateName);
        }
        return TemplateLoader.getTemplateContent(globalTemplateName);
    }

    private Templates templates(Config local) {
        if (config.templatesMap.containsKey(local.selector)) {
            return config.templatesMap.get(local.selector);
        }
        return empty;
    }

    public void processFields(Source source, Class<?> klass, CompoundParams global, List<Field> fields, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "n", "" + fields.size()
        );
        int i = 0;
        for (final var field : fields) {
            final var fieldType = field.getType();
            final var name = field.getName();
            segment.param(
                "field." + i + ".GenericString", field.toGenericString(),
                "field." + i + ".ClassSimpleName", fieldType.getSimpleName(),
                "field." + i + ".ClassName", fieldType.getName(),
                "field." + i + ".ClassCanonicalName", fieldType.getCanonicalName(),
                "field." + i + ".ClassPackage", fieldType.getPackageName(),
                "field." + i + ".ClassTypeName", fieldType.getTypeName(),
                "field." + i + ".ClassGenericString", fieldType.toGenericString()
            );
            i++;
            segment.param(
                "field." + name + ".GenericString", field.toGenericString(),
                "field." + name + ".ClassSimpleName", fieldType.getSimpleName(),
                "field." + name + ".ClassName", fieldType.getName(),
                "field." + name + ".ClassCanonicalName", fieldType.getCanonicalName(),
                "field." + name + ".ClassPackage", fieldType.getPackageName(),
                "field." + name + ".ClassTypeName", fieldType.getTypeName(),
                "field." + name + ".ClassGenericString", fieldType.toGenericString()
            );
        }
        segment.write(getTemplateContent(local.processFields, templates(local).processFields));
    }

    public void processMethods(Source source, Class<?> klass, CompoundParams global, List<Method> methods, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "n", "" + methods.size()
        );
        int i = 0;
        for (final var method : methods) {
            final var returnType = method.getReturnType();
            final var name = method.getName();
            segment.param(
                "method." + i + ".GenericString", method.toGenericString(),
                "method." + i + ".ReturnClassSimpleName", returnType.getSimpleName(),
                "method." + i + ".ReturnClassName", returnType.getName(),
                "method." + i + ".ReturnClassCanonicalName", returnType.getCanonicalName(),
                "method." + i + ".ReturnClassPackage", returnType.getPackageName(),
                "method." + i + ".ReturnClassTypeName", returnType.getTypeName(),
                "method." + i + ".ReturnClassGenericString", returnType.toGenericString()
            );
            i++;
            segment.param(
                "method." + name + ".GenericString", method.toGenericString(),
                "method." + name + ".ReturnClassSimpleName", returnType.getSimpleName(),
                "method." + name + ".ReturnClassName", returnType.getName(),
                "method." + name + ".ReturnClassCanonicalName", returnType.getCanonicalName(),
                "method." + name + ".ReturnClassPackage", returnType.getPackageName(),
                "method." + name + ".ReturnClassTypeName", returnType.getTypeName(),
                "method." + name + ".ReturnClassGenericString", returnType.toGenericString()
            );
        }
        segment.write(getTemplateContent(local.processMethods, templates(local).processMethods));
    }

    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.write(getTemplateContent(local.postprocess, templates(local).postprocess));
    }

    @Override
    public String mnemonic() {
        return "templated";
    }

    //<editor-fold id="configBuilder" generateImplementedKeys="false">
    private final Config config = new Config();

    public static TemplateBasedSelectedMemberGenerator.Builder builder() {
        return new TemplateBasedSelectedMemberGenerator().new Builder();
    }

    public class Builder {
        public Builder declaredOnly(boolean declaredOnly) {
            config.declaredOnly = declaredOnly;
            return this;
        }

        public Builder fieldFilter(String fieldFilter) {
            config.fieldFilter = fieldFilter;
            return this;
        }

        public Builder generatedAnnotation(Class generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder methodFilter(String methodFilter) {
            config.methodFilter = methodFilter;
            return this;
        }

        public Builder postprocess(String postprocess) {
            config.setPostprocess(postprocess);
            return this;
        }

        public Builder preprocess(String preprocess) {
            config.setPreprocess(preprocess);
            return this;
        }

        public Builder processField(String processField) {
            config.setProcessField(processField);
            return this;
        }

        public Builder processFields(String processFields) {
            config.setProcessFields(processFields);
            return this;
        }

        public Builder processMethod(String processMethod) {
            config.setProcessMethod(processMethod);
            return this;
        }

        public Builder processMethods(String processMethods) {
            config.setProcessMethods(processMethods);
            return this;
        }

        public Builder selector(String selector) {
            config.selector = selector;
            return this;
        }

        public TemplateBasedSelectedMemberGenerator build() {
            return TemplateBasedSelectedMemberGenerator.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.declaredOnly = config.declaredOnly;
        local.fieldFilter = params.get("fieldFilter", config.fieldFilter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.methodFilter = params.get("methodFilter", config.methodFilter);
        local.setPostprocess(params.get("postprocess", config.postprocess));
        local.setPreprocess(params.get("preprocess", config.preprocess));
        local.setProcessField(params.get("processField", config.processField));
        local.setProcessFields(params.get("processFields", config.processFields));
        local.setProcessMethod(params.get("processMethod", config.processMethod));
        local.setProcessMethods(params.get("processMethods", config.processMethods));
        local.selector = params.get("selector", config.selector);
        return local;
    }
    //</editor-fold>
}
