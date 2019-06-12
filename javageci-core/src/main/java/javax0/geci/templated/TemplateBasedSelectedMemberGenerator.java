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
import java.util.*;
import java.util.stream.Collectors;

public class TemplateBasedSelectedMemberGenerator extends AbstractJavaGenerator {

    @Override
    public boolean activeIn(int phase) {
        this.phase = phase;
        return phase < 2;
    }

    @Override
    public int phases() {
        return 2;
    }

    public interface Consumer3<A, B, C> {
        void accept(A a, B b, C c);
    }

    public interface Consumer4<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }

    private class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String fieldFilter = "true";
        private String methodFilter = "true";
        private String classFilter = "true";
        private boolean declaredOnly = true;
        private String selector = "";
        private final Map<String, Templates> templatesMap = new HashMap<>();
        private String preprocess = null;
        private String processField = null;
        private String processMethod = null;
        private String processFields = null;
        private String processMethods = null;
        private String postprocess = null;
        private String processClass = null;
        private String preprocessClass = null;
        private String postprocessClass = null;
        private Consumer3<Source, Class, Segment> preprocessParams = null;
        private Consumer4<Source, Class, Field, Segment> processFieldParams = null;
        private Consumer4<Source, Class, Method, Segment> processMethodParams = null;
        private Consumer3<Source, Class, Segment> processFieldsParams = null;
        private Consumer3<Source, Class, Segment> processMethodsParams = null;
        private Consumer3<Source, Class, Segment> preprocessClassParams = null;
        private Consumer3<Source, Class, Segment> postprocessClassParams = null;
        private Consumer4<Source, Class, Class, Segment> processClassParams = null;

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
        private String processClass = null;
        private String preprocessClass = null;
        private String postprocessClass = null;
    }

    @Override
    public final void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var segment = source.open(global.id());
        if (phase == 0) {
            final var fields = config.declaredOnly ? GeciReflectionTools.getDeclaredFieldsSorted(klass) : GeciReflectionTools.getAllFieldsSorted(klass);
            final var methods = config.declaredOnly ? GeciReflectionTools.getDeclaredMethodsSorted(klass) : GeciReflectionTools.getAllMethodsSorted(klass);
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
        } else {
            var local = localConfig(global);
            final var selector = Selector.compile(local.classFilter);
            final var selectedClasses = new ArrayList<>(classes.stream().filter(selector::match).collect(Collectors.toList()));
            selectedClasses.sort(Comparator.comparing(Class::getName));
            preprocessClass(source, klass, selectedClasses, global, segment);

            for (final var listedKlass : selectedClasses) {
                processClass(source, klass, listedKlass, global, segment);
            }
            postprocessClass(source, klass, selectedClasses, global, segment);
        }
    }

    private void preprocessClass(Source source, Class<?> klass, List<Class<?>> selectedClasses, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        if (config.preprocessClassParams != null) {
            config.preprocessClassParams.accept(source, klass, segment);
        }
        segment.write(getTemplateContent(local.preprocessClass, templates(local).preprocessClass));
    }

    public void processClass(Source source, Class<?> klass, Class<?> listedClass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "class.SimpleName", listedClass.getSimpleName(),
            "class.Name", listedClass.getName(),
            "class.CanonicalName", listedClass.getCanonicalName(),
            "class.Package", listedClass.getPackageName(),
            "class.TypeName", listedClass.getTypeName(),
            "class.GenericString", listedClass.toGenericString()
        );
        if (config.processClassParams != null) {
            config.processClassParams.accept(source, klass, listedClass, segment);
        }
        segment.write(getTemplateContent(local.processClass, templates(local).processClass));
    }

    private void postprocessClass(Source source, Class<?> klass, List<Class<?>> selectedClasses, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        if (config.postprocessClassParams != null) {
            config.postprocessClassParams.accept(source, klass, segment);
        }
        segment.write(getTemplateContent(local.postprocessClass, templates(local).postprocessClass));
    }

    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "this.SimpleName", klass.getSimpleName(),
            "this.Name", klass.getName(),
            "this.CanonicalName", klass.getCanonicalName(),
            "this.Package", klass.getPackageName(),
            "this.TypeName", klass.getTypeName(),
            "this.GenericString", klass.toGenericString()
        );
        if (config.preprocessParams != null) {
            config.preprocessParams.accept(source, klass, segment);
        }
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
        if (config.processFieldParams != null) {
            config.processFieldParams.accept(source, klass, field, segment);
        }
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
        if (config.processMethodParams != null) {
            config.processMethodParams.accept(source, klass, method, segment);
        }
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
            "fields.n", "" + fields.size()
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
        if (config.processFieldsParams != null) {
            config.processFieldsParams.accept(source, klass, segment);
        }
        segment.write(getTemplateContent(local.processFields, templates(local).processFields));
    }

    public void processMethods(Source source, Class<?> klass, CompoundParams global, List<Method> methods, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "methods.n", "" + methods.size()
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
        if (config.processMethodsParams != null) {
            config.processMethodsParams.accept(source, klass, segment);
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

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static TemplateBasedSelectedMemberGenerator.Builder builder() {
        return new TemplateBasedSelectedMemberGenerator().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "classFilter",
        "fieldFilter",
        "methodFilter",
        "postprocess",
        "postprocessClass",
        "preprocess",
        "preprocessClass",
        "processClass",
        "processField",
        "processFields",
        "processMethod",
        "processMethods",
        "selector",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder classFilter(String classFilter) {
            config.classFilter = classFilter;
            return this;
        }

        public Builder declaredOnly(boolean declaredOnly) {
            config.declaredOnly = declaredOnly;
            return this;
        }

        public Builder fieldFilter(String fieldFilter) {
            config.fieldFilter = fieldFilter;
            return this;
        }

        public Builder generatedAnnotation(Class<? extends java.lang.annotation.Annotation> generatedAnnotation) {
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

        public Builder postprocessClass(String postprocessClass) {
            config.postprocessClass = postprocessClass;
            return this;
        }

        public Builder postprocessClassParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer3<javax0.geci.api.Source,Class,javax0.geci.api.Segment> postprocessClassParams) {
            config.postprocessClassParams = postprocessClassParams;
            return this;
        }

        public Builder preprocess(String preprocess) {
            config.setPreprocess(preprocess);
            return this;
        }

        public Builder preprocessClass(String preprocessClass) {
            config.preprocessClass = preprocessClass;
            return this;
        }

        public Builder preprocessClassParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer3<javax0.geci.api.Source,Class,javax0.geci.api.Segment> preprocessClassParams) {
            config.preprocessClassParams = preprocessClassParams;
            return this;
        }

        public Builder preprocessParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer3<javax0.geci.api.Source,Class,javax0.geci.api.Segment> preprocessParams) {
            config.preprocessParams = preprocessParams;
            return this;
        }

        public Builder processClass(String processClass) {
            config.processClass = processClass;
            return this;
        }

        public Builder processClassParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer4<javax0.geci.api.Source,Class,Class,javax0.geci.api.Segment> processClassParams) {
            config.processClassParams = processClassParams;
            return this;
        }

        public Builder processField(String processField) {
            config.setProcessField(processField);
            return this;
        }

        public Builder processFieldParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer4<javax0.geci.api.Source,Class,java.lang.reflect.Field,javax0.geci.api.Segment> processFieldParams) {
            config.processFieldParams = processFieldParams;
            return this;
        }

        public Builder processFields(String processFields) {
            config.setProcessFields(processFields);
            return this;
        }

        public Builder processFieldsParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer3<javax0.geci.api.Source,Class,javax0.geci.api.Segment> processFieldsParams) {
            config.processFieldsParams = processFieldsParams;
            return this;
        }

        public Builder processMethod(String processMethod) {
            config.setProcessMethod(processMethod);
            return this;
        }

        public Builder processMethodParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer4<javax0.geci.api.Source,Class,java.lang.reflect.Method,javax0.geci.api.Segment> processMethodParams) {
            config.processMethodParams = processMethodParams;
            return this;
        }

        public Builder processMethods(String processMethods) {
            config.setProcessMethods(processMethods);
            return this;
        }

        public Builder processMethodsParams(javax0.geci.templated.TemplateBasedSelectedMemberGenerator.Consumer3<javax0.geci.api.Source,Class,javax0.geci.api.Segment> processMethodsParams) {
            config.processMethodsParams = processMethodsParams;
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
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.classFilter = params.get("classFilter",config.classFilter);
        local.declaredOnly = config.declaredOnly;
        local.fieldFilter = params.get("fieldFilter",config.fieldFilter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.methodFilter = params.get("methodFilter",config.methodFilter);
        local.setPostprocess(params.get("postprocess",config.postprocess));
        local.postprocessClass = params.get("postprocessClass",config.postprocessClass);
        local.postprocessClassParams = config.postprocessClassParams;
        local.setPreprocess(params.get("preprocess",config.preprocess));
        local.preprocessClass = params.get("preprocessClass",config.preprocessClass);
        local.preprocessClassParams = config.preprocessClassParams;
        local.preprocessParams = config.preprocessParams;
        local.processClass = params.get("processClass",config.processClass);
        local.processClassParams = config.processClassParams;
        local.setProcessField(params.get("processField",config.processField));
        local.processFieldParams = config.processFieldParams;
        local.setProcessFields(params.get("processFields",config.processFields));
        local.processFieldsParams = config.processFieldsParams;
        local.setProcessMethod(params.get("processMethod",config.processMethod));
        local.processMethodParams = config.processMethodParams;
        local.setProcessMethods(params.get("processMethods",config.processMethods));
        local.processMethodsParams = config.processMethodsParams;
        local.selector = params.get("selector",config.selector);
        return local;
    }
    //</editor-fold>
}
