package javax0.geci.templated;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.TemplateLoader;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AnnotationBuilder
@Geci("repeated values='preprocess,processField,processMethod,processClass," +
    "processMemberClass,processMethods,processClasses,processFields," +
    "postprocess,preprocessClass,postprocessClass'")
public class Templated extends AbstractJavaGenerator {

    @Override
    public boolean activeIn(int phase) {
        this.phase = phase;
        return phase < 2;
    }

    @Override
    public int phases() {
        return 2;
    }

    private static final Consumer NOOP = a -> {
    };
    private static final BiConsumer BiNOOP = (s, a) -> {
    };
    private static final BiFunction<Context, String, String> BiFuNOOP = (ctx, s) -> s;

    private static class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private Context ctx = new Triplet();
        private String fieldFilter = "true";
        private String methodFilter = "true";
        private String classFilter = "true";
        private String memberClassFilter = "true";
        private boolean declaredOnly = true;
        private String selector = "";
        private final Map<String, Templates> templatesMap = new HashMap<>();

        /*TEMPLATE configTemplates
        private String {{value}} = null;
         */
        //<editor-fold id="configTemplates">
        private String preprocess = null;
        private String processField = null;
        private String processMethod = null;
        private String processClass = null;
        private String processMemberClass = null;
        private String processMethods = null;
        private String processClasses = null;
        private String processFields = null;
        private String postprocess = null;
        private String preprocessClass = null;
        private String postprocessClass = null;
        //</editor-fold>

        /*TEMPLATE consumers
        private {{type}} {{value}}Params = {{const}};
         */
        //<editor-fold id="consumers">
        private Consumer<Context> preprocessParams = NOOP;
        private BiConsumer<Context, Field> processFieldParams = BiNOOP;
        private BiConsumer<Context, Method> processMethodParams = BiNOOP;
        private BiConsumer<Context, Class> processClassParams = BiNOOP;
        private BiConsumer<Context, Class> processMemberClassParams = BiNOOP;
        private Consumer<Context> processMethodsParams = NOOP;
        private Consumer<Context> processClassesParams = NOOP;
        private Consumer<Context> processFieldsParams = NOOP;
        private Consumer<Context> postprocessParams = NOOP;
        private Consumer<Context> preprocessClassParams = NOOP;
        private Consumer<Context> postprocessClassParams = NOOP;
        //</editor-fold>

        /*TEMPLATE bifunctions
        private BiFunction<Context, String, String> {{value}}Resolv = BiFuNOOP;
         */
        //<editor-fold id="bifunctions">
        private BiFunction<Context, String, String> preprocessResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processFieldResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processMethodResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processClassResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processMemberClassResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processMethodsResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processClassesResolv = BiFuNOOP;
        private BiFunction<Context, String, String> processFieldsResolv = BiFuNOOP;
        private BiFunction<Context, String, String> postprocessResolv = BiFuNOOP;
        private BiFunction<Context, String, String> preprocessClassResolv = BiFuNOOP;
        private BiFunction<Context, String, String> postprocessClassResolv = BiFuNOOP;
        //</editor-fold>

        private Templates templates() {
            if (!templatesMap.containsKey(selector)) {
                templatesMap.put(selector, new Templates());
            }
            return templatesMap.get(selector);
        }

        /*TEMPLATE configSetters
        private void {{setter}}(String template) {
            templates().{{value}} = template;
        }
        
         */
        //<editor-fold id="configSetters">
        private void setPreprocess(String template) {
            templates().preprocess = template;
        }

        private void setProcessField(String template) {
            templates().processField = template;
        }

        private void setProcessMethod(String template) {
            templates().processMethod = template;
        }

        private void setProcessClass(String template) {
            templates().processClass = template;
        }

        private void setProcessMemberClass(String template) {
            templates().processMemberClass = template;
        }

        private void setProcessMethods(String template) {
            templates().processMethods = template;
        }

        private void setProcessClasses(String template) {
            templates().processClasses = template;
        }

        private void setProcessFields(String template) {
            templates().processFields = template;
        }

        private void setPostprocess(String template) {
            templates().postprocess = template;
        }

        private void setPreprocessClass(String template) {
            templates().preprocessClass = template;
        }

        private void setPostprocessClass(String template) {
            templates().postprocessClass = template;
        }

        //</editor-fold>
    }

    private static final Templates EMPTY = new Templates();

    private static class Templates {
        /*TEMPLATES templates
        private String {{value}} = null;
         */
        //<editor-fold id="templates">
        private String preprocess = null;
        private String processField = null;
        private String processMethod = null;
        private String processClass = null;
        private String processMemberClass = null;
        private String processMethods = null;
        private String processClasses = null;
        private String processFields = null;
        private String postprocess = null;
        private String preprocessClass = null;
        private String postprocessClass = null;
        //</editor-fold>
    }

    @Override
    public final void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try (final var segment = source.open(global.id())) {
            if (phase == 0) {
                preprocess(source, klass, global, segment);

                final var selectedFields = new ArrayList<Field>();
                final var fields = config.declaredOnly ? GeciReflectionTools.getDeclaredFieldsSorted(klass)
                    : GeciReflectionTools.getAllFieldsSorted(klass);
                processFieldsLooping(source, klass, global, segment, fields, selectedFields);

                final var selectedMethods = new ArrayList<Method>();
                final var methods = config.declaredOnly ? GeciReflectionTools.getDeclaredMethodsSorted(klass)
                    : GeciReflectionTools.getAllMethodsSorted(klass);
                processMethodsLooping(source, klass, global, segment, methods, selectedMethods);

                final var selectedClasses = new ArrayList<Class>();
                final var classes = config.declaredOnly ? GeciReflectionTools.getDeclaredClassesSorted(klass)
                    : GeciReflectionTools.getAllClassesSorted(klass);
                processMemberClassesLooping(source, klass, global, segment, classes, selectedClasses);

                processFields(source, klass, global, selectedFields, segment);
                processMethods(source, klass, global, selectedMethods, segment);
                processClasses(source, klass, global, selectedClasses, segment);

            } else {
                var local = localConfig(global);
                final var selector = Selector.compile(local.classFilter);
                final var selectedClasses = classes.stream()
                    .filter(selector::match)
                    .sorted(Comparator.comparing(Class::getName))
                    .collect(Collectors.toCollection(ArrayList::new));
                preprocessClass(source, klass, selectedClasses, global, segment);

                for (final var listedKlass : selectedClasses) {
                    processClass(source, klass, listedKlass, global, segment);
                }
                postprocessClass(source, klass, selectedClasses, global, segment);
                postprocess(source, klass, global, segment);
            }
        }
    }

    private void processMethodsLooping(Source source,
                                       Class<?> klass,
                                       CompoundParams global,
                                       Segment segment,
                                       Method[] methods,
                                       List<Method> selectedMethods)
        throws Exception {
        for (final var method : methods) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(method, mnemonic()), global);
            var local = localConfig(params);
            if (Selector.compile(local.methodFilter).match(method)) {
                selectedMethods.add(method);
                process(source, klass, params, method, segment);
            }
        }
    }

    private void processFieldsLooping(Source source,
                                      Class<?> klass,
                                      CompoundParams global,
                                      Segment segment,
                                      Field[] fields,
                                      List<Field> selectedFields) {
        for (final var field : fields) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(field, mnemonic()), global);
            var local = localConfig(params);
            if (Selector.compile(local.fieldFilter).match(field)) {
                selectedFields.add(field);
                process(source, klass, params, field, segment);
            }
        }
    }

    private void processMemberClassesLooping(Source source,
                                             Class<?> klass,
                                             CompoundParams global,
                                             Segment segment,
                                             Class[] classes,
                                             List<Class> selectedClasses) {
        for (final var memberClass : classes) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(memberClass, mnemonic()), global);
            var local = localConfig(params);
            if (Selector.compile(local.memberClassFilter).match(memberClass)) {
                selectedClasses.add(memberClass);
                process(source, klass, params, memberClass, segment);
            }
        }
    }

    private void preprocessClass(Source source, Class<?> klass, List<Class<?>> selectedClasses, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        setTripletInContext(source, klass, segment);
        config.preprocessClassParams.accept(config.ctx);
        segment.write(
            config.preprocessClassResolv.apply(config.ctx,
                getTemplateContent(local.preprocessClass, templates(local).preprocessClass)));
    }

    public void processClass(Source source, Class<?> klass, Class<?> listedClass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        setParams(segment, "class.",
            "SimpleName", listedClass.getSimpleName(),
            "Name", listedClass.getName(),
            "CanonicalName", listedClass.getCanonicalName(),
            "Package", listedClass.getPackageName(),
            "TypeName", listedClass.getTypeName(),
            "GenericString", listedClass.toGenericString()
        );
        setTripletInContext(source, klass, segment);
        config.processClassParams.accept(config.ctx, listedClass);
        segment.write(
            config.processClassesResolv.apply(config.ctx,
                getTemplateContent(local.processClass, templates(local).processClass)));
    }

    private void postprocessClass(Source source, Class<?> klass, List<Class<?>> classes, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "classes.n", "" + classes.size()
        );
        int i = 0;
        for (final var selectedClass : classes) {
            setParams(segment, "class." + i + ".",
                "SimpleName", selectedClass.getSimpleName(),
                "Name", selectedClass.getName(),
                "CanonicalName", selectedClass.getCanonicalName(),
                "Package", selectedClass.getPackageName(),
                "TypeName", selectedClass.getTypeName(),
                "GenericString", selectedClass.toGenericString()
            );
            i++;
            setParams(segment, "class." + selectedClass.getName() + ".",
                "SimpleName", selectedClass.getSimpleName(),
                "Name", selectedClass.getName(),
                "CanonicalName", selectedClass.getCanonicalName(),
                "Package", selectedClass.getPackageName(),
                "TypeName", selectedClass.getTypeName(),
                "GenericString", selectedClass.toGenericString()
            );
        }
        setTripletInContext(source, klass, segment);
        config.postprocessClassParams.accept(config.ctx);
        segment.write(
            config.postprocessClassResolv.apply(config.ctx,
                getTemplateContent(local.postprocessClass, templates(local).postprocessClass)));
    }

    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        setParams(segment, "this.",
            "SimpleName", klass.getSimpleName(),
            "Name", klass.getName(),
            "CanonicalName", klass.getCanonicalName(),
            "Package", klass.getPackageName(),
            "TypeName", klass.getTypeName(),
            "GenericString", klass.toGenericString()
        );
        setTripletInContext(source, klass, segment);
        config.preprocessParams.accept(config.ctx);
        for (final var key : global.keySet()) {
            segment.param(key, global.get(key));
            segment.param("global." + key, global.get(key));
        }
        segment.write(
            config.preprocessResolv.apply(config.ctx,
                getTemplateContent(local.preprocess, templates(local).preprocess)));
    }

    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var fieldType = field.getType();
        setParams(segment, "field.",
            "name", field.getName(),
            "genericString", field.toGenericString(),
            "classSimpleName", fieldType.getSimpleName(),
            "className", fieldType.getName(),
            "classCanonicalName", fieldType.getCanonicalName(),
            "classPackage", fieldType.getPackageName(),
            "classTypeName", fieldType.getTypeName(),
            "classGenericString", fieldType.toGenericString()
        );
        setTripletInContext(source, klass, segment);
        config.processFieldParams.accept(config.ctx, field);
        for (final var key : params.keySet()) {
            segment.param(key, params.get(key));
        }
        segment.write(
            config.processFieldResolv.apply(config.ctx,
                getTemplateContent(local.processField, templates(local).processField)));
        for (final var key : segment.paramKeySet()) {
            if (key.startsWith("field.")) {
                segment.param(key, null);
            }
        }
    }


    public void process(Source source, Class<?> klass, CompoundParams params, Class memberClass, Segment segment) {
        final var local = localConfig(params);
        setParams(segment, "memberClass.",
            "SimpleName", memberClass.getSimpleName(),
            "Name", memberClass.getName(),
            "CanonicalName", memberClass.getCanonicalName(),
            "Package", memberClass.getPackageName(),
            "TypeName", memberClass.getTypeName(),
            "GenericString", memberClass.toGenericString()
        );
        setTripletInContext(source, klass, segment);
        config.processMemberClassParams.accept(config.ctx, memberClass);
        for (final var key : params.keySet()) {
            segment.param(key, params.get(key));
        }
        segment.write(
            config.processMemberClassResolv.apply(config.ctx,
                getTemplateContent(local.processMemberClass, templates(local).processMemberClass)));
        for (final var key : segment.paramKeySet()) {
            if (key.startsWith("memberClass.")) {
                segment.param(key, null);
            }
        }
    }

    public void process(Source source, Class<?> klass, CompoundParams params, Method method, Segment segment) {
        final var local = localConfig(params);
        final var returnType = method.getReturnType();
        setParams(segment, "method.",
            "name", method.getName(),
            "genericString", method.toGenericString(),
            "returnClassSimpleName", returnType.getSimpleName(),
            "returnClassName", returnType.getName(),
            "returnClassCanonicalName", returnType.getCanonicalName(),
            "returnClassPackage", returnType.getPackageName(),
            "returnClassTypeName", returnType.getTypeName(),
            "returnClassGenericString", returnType.toGenericString()
        );
        setTripletInContext(source, klass, segment);
        config.processMethodParams.accept(config.ctx, method);
        for (final var key : params.keySet()) {
            segment.param(key, params.get(key));
        }
        segment.write(
            config.processMethodResolv.apply(config.ctx,
                getTemplateContent(local.processMethod, templates(local).processMethod)));
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
        return EMPTY;
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
            setParams(segment, "field." + i + ".",
                "GenericString", field.toGenericString(),
                "ClassSimpleName", fieldType.getSimpleName(),
                "ClassName", fieldType.getName(),
                "ClassCanonicalName", fieldType.getCanonicalName(),
                "ClassPackage", fieldType.getPackageName(),
                "ClassTypeName", fieldType.getTypeName(),
                "ClassGenericString", fieldType.toGenericString()
            );
            i++;
            setParams(segment, "field." + name + ".",
                "GenericString", field.toGenericString(),
                "ClassSimpleName", fieldType.getSimpleName(),
                "ClassName", fieldType.getName(),
                "ClassCanonicalName", fieldType.getCanonicalName(),
                "ClassPackage", fieldType.getPackageName(),
                "ClassTypeName", fieldType.getTypeName(),
                "ClassGenericString", fieldType.toGenericString()
            );
        }
        setTripletInContext(source, klass, segment);
        config.processFieldsParams.accept(config.ctx);
        segment.write(
            config.processFieldResolv.apply(config.ctx,
                getTemplateContent(local.processFields, templates(local).processFields)));
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
            setParams(segment, "method." + i + ".",
                "GenericString", method.toGenericString(),
                "ReturnClassSimpleName", returnType.getSimpleName(),
                "ReturnClassName", returnType.getName(),
                "ReturnClassCanonicalName", returnType.getCanonicalName(),
                "ReturnClassPackage", returnType.getPackageName(),
                "ReturnClassTypeName", returnType.getTypeName(),
                "ReturnClassGenericString", returnType.toGenericString()
            );
            i++;
            setParams(segment, "method." + name + ".",
                "GenericString", method.toGenericString(),
                "ReturnClassSimpleName", returnType.getSimpleName(),
                "ReturnClassName", returnType.getName(),
                "ReturnClassCanonicalName", returnType.getCanonicalName(),
                "ReturnClassPackage", returnType.getPackageName(),
                "ReturnClassTypeName", returnType.getTypeName(),
                "ReturnClassGenericString", returnType.toGenericString()
            );
        }
        setTripletInContext(source, klass, segment);
        config.processMethodsParams.accept(config.ctx);
        segment.write(
            config.processMethodsResolv.apply(config.ctx,
                getTemplateContent(local.processMethods, templates(local).processMethods)));
    }

    public void processClasses(Source source, Class<?> klass, CompoundParams global, List<Class> classes, Segment segment) {
        final var local = localConfig(global);
        segment.param(
            "memberClasses.n", "" + classes.size()
        );
        int i = 0;
        for (final var memberClass : classes) {
            setParams(segment, "memberClass." + i + ".",
                "SimpleName", memberClass.getSimpleName(),
                "Name", memberClass.getName(),
                "CanonicalName", memberClass.getCanonicalName(),
                "Package", memberClass.getPackageName(),
                "TypeName", memberClass.getTypeName(),
                "GenericString", memberClass.toGenericString()
            );
            i++;
            setParams(segment, "memberClass." + memberClass.getName() + ".",
                "SimpleName", memberClass.getSimpleName(),
                "Name", memberClass.getName(),
                "CanonicalName", memberClass.getCanonicalName(),
                "Package", memberClass.getPackageName(),
                "TypeName", memberClass.getTypeName(),
                "GenericString", memberClass.toGenericString()
            );
        }
        setTripletInContext(source, klass, segment);
        config.processClassesParams.accept(config.ctx);
        segment.write(
            config.processClassesResolv.apply(config.ctx,
                getTemplateContent(local.processClasses, templates(local).processClasses)));
    }

    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);

        setTripletInContext(source, klass, segment);
        config.postprocessParams.accept(config.ctx);
        segment.write(
            config.postprocessResolv.apply(config.ctx,
                getTemplateContent(local.postprocess, templates(local).postprocess)));
    }

    private void setTripletInContext(Source source, Class<?> klass, Segment segment) {
        config.ctx.triplet(source, klass, segment);
    }

    private static void setParams(Segment segment, String prefix, String... keyValuePairs) {
        if (keyValuePairs.length % 2 == 1) {
            throw new IllegalArgumentException("Parameters to Segment.param() should be in pair");
        }
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            segment.param(prefix + keyValuePairs[i], keyValuePairs[i + 1]);
        }
    }

    @Override
    public String mnemonic() {
        return "templated";
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public static Templated.Builder builder() {
        return new Templated().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "classFilter",
        "fieldFilter",
        "memberClassFilter",
        "methodFilter",
        "postprocess",
        "postprocessClass",
        "preprocess",
        "preprocessClass",
        "processClass",
        "processClasses",
        "processField",
        "processFields",
        "processMemberClass",
        "processMethod",
        "processMethods",
        "selector",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }

    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder classFilter(String classFilter) {
            config.classFilter = classFilter;
            return this;
        }

        public Builder ctx(javax0.geci.templated.Context ctx) {
            config.ctx = ctx;
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

        public Builder memberClassFilter(String memberClassFilter) {
            config.memberClassFilter = memberClassFilter;
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
            config.setPostprocessClass(postprocessClass);
            return this;
        }

        public Builder postprocessClassParams(java.util.function.Consumer<javax0.geci.templated.Context> postprocessClassParams) {
            config.postprocessClassParams = postprocessClassParams;
            return this;
        }

        public Builder postprocessClassResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> postprocessClassResolv) {
            config.postprocessClassResolv = postprocessClassResolv;
            return this;
        }

        public Builder postprocessParams(java.util.function.Consumer<javax0.geci.templated.Context> postprocessParams) {
            config.postprocessParams = postprocessParams;
            return this;
        }

        public Builder postprocessResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> postprocessResolv) {
            config.postprocessResolv = postprocessResolv;
            return this;
        }

        public Builder preprocess(String preprocess) {
            config.setPreprocess(preprocess);
            return this;
        }

        public Builder preprocessClass(String preprocessClass) {
            config.setPreprocessClass(preprocessClass);
            return this;
        }

        public Builder preprocessClassParams(java.util.function.Consumer<javax0.geci.templated.Context> preprocessClassParams) {
            config.preprocessClassParams = preprocessClassParams;
            return this;
        }

        public Builder preprocessClassResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> preprocessClassResolv) {
            config.preprocessClassResolv = preprocessClassResolv;
            return this;
        }

        public Builder preprocessParams(java.util.function.Consumer<javax0.geci.templated.Context> preprocessParams) {
            config.preprocessParams = preprocessParams;
            return this;
        }

        public Builder preprocessResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> preprocessResolv) {
            config.preprocessResolv = preprocessResolv;
            return this;
        }

        public Builder processClass(String processClass) {
            config.setProcessClass(processClass);
            return this;
        }

        public Builder processClassParams(java.util.function.BiConsumer<javax0.geci.templated.Context, Class> processClassParams) {
            config.processClassParams = processClassParams;
            return this;
        }

        public Builder processClassResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processClassResolv) {
            config.processClassResolv = processClassResolv;
            return this;
        }

        public Builder processClasses(String processClasses) {
            config.setProcessClasses(processClasses);
            return this;
        }

        public Builder processClassesParams(java.util.function.Consumer<javax0.geci.templated.Context> processClassesParams) {
            config.processClassesParams = processClassesParams;
            return this;
        }

        public Builder processClassesResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processClassesResolv) {
            config.processClassesResolv = processClassesResolv;
            return this;
        }

        public Builder processField(String processField) {
            config.setProcessField(processField);
            return this;
        }

        public Builder processFieldParams(java.util.function.BiConsumer<javax0.geci.templated.Context, java.lang.reflect.Field> processFieldParams) {
            config.processFieldParams = processFieldParams;
            return this;
        }

        public Builder processFieldResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processFieldResolv) {
            config.processFieldResolv = processFieldResolv;
            return this;
        }

        public Builder processFields(String processFields) {
            config.setProcessFields(processFields);
            return this;
        }

        public Builder processFieldsParams(java.util.function.Consumer<javax0.geci.templated.Context> processFieldsParams) {
            config.processFieldsParams = processFieldsParams;
            return this;
        }

        public Builder processFieldsResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processFieldsResolv) {
            config.processFieldsResolv = processFieldsResolv;
            return this;
        }

        public Builder processMemberClass(String processMemberClass) {
            config.setProcessMemberClass(processMemberClass);
            return this;
        }

        public Builder processMemberClassParams(java.util.function.BiConsumer<javax0.geci.templated.Context, Class> processMemberClassParams) {
            config.processMemberClassParams = processMemberClassParams;
            return this;
        }

        public Builder processMemberClassResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processMemberClassResolv) {
            config.processMemberClassResolv = processMemberClassResolv;
            return this;
        }

        public Builder processMethod(String processMethod) {
            config.setProcessMethod(processMethod);
            return this;
        }

        public Builder processMethodParams(java.util.function.BiConsumer<javax0.geci.templated.Context, java.lang.reflect.Method> processMethodParams) {
            config.processMethodParams = processMethodParams;
            return this;
        }

        public Builder processMethodResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processMethodResolv) {
            config.processMethodResolv = processMethodResolv;
            return this;
        }

        public Builder processMethods(String processMethods) {
            config.setProcessMethods(processMethods);
            return this;
        }

        public Builder processMethodsParams(java.util.function.Consumer<javax0.geci.templated.Context> processMethodsParams) {
            config.processMethodsParams = processMethodsParams;
            return this;
        }

        public Builder processMethodsResolv(java.util.function.BiFunction<javax0.geci.templated.Context, String, String> processMethodsResolv) {
            config.processMethodsResolv = processMethodsResolv;
            return this;
        }

        public Builder selector(String selector) {
            config.selector = selector;
            return this;
        }

        public Templated build() {
            return Templated.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.classFilter = params.get("classFilter", config.classFilter);
        local.ctx = config.ctx;
        local.declaredOnly = config.declaredOnly;
        local.fieldFilter = params.get("fieldFilter", config.fieldFilter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.memberClassFilter = params.get("memberClassFilter", config.memberClassFilter);
        local.methodFilter = params.get("methodFilter", config.methodFilter);
        local.setPostprocess(params.get("postprocess", config.postprocess));
        local.setPostprocessClass(params.get("postprocessClass", config.postprocessClass));
        local.postprocessClassParams = config.postprocessClassParams;
        local.postprocessClassResolv = config.postprocessClassResolv;
        local.postprocessParams = config.postprocessParams;
        local.postprocessResolv = config.postprocessResolv;
        local.setPreprocess(params.get("preprocess", config.preprocess));
        local.setPreprocessClass(params.get("preprocessClass", config.preprocessClass));
        local.preprocessClassParams = config.preprocessClassParams;
        local.preprocessClassResolv = config.preprocessClassResolv;
        local.preprocessParams = config.preprocessParams;
        local.preprocessResolv = config.preprocessResolv;
        local.setProcessClass(params.get("processClass", config.processClass));
        local.processClassParams = config.processClassParams;
        local.processClassResolv = config.processClassResolv;
        local.setProcessClasses(params.get("processClasses", config.processClasses));
        local.processClassesParams = config.processClassesParams;
        local.processClassesResolv = config.processClassesResolv;
        local.setProcessField(params.get("processField", config.processField));
        local.processFieldParams = config.processFieldParams;
        local.processFieldResolv = config.processFieldResolv;
        local.setProcessFields(params.get("processFields", config.processFields));
        local.processFieldsParams = config.processFieldsParams;
        local.processFieldsResolv = config.processFieldsResolv;
        local.setProcessMemberClass(params.get("processMemberClass", config.processMemberClass));
        local.processMemberClassParams = config.processMemberClassParams;
        local.processMemberClassResolv = config.processMemberClassResolv;
        local.setProcessMethod(params.get("processMethod", config.processMethod));
        local.processMethodParams = config.processMethodParams;
        local.processMethodResolv = config.processMethodResolv;
        local.setProcessMethods(params.get("processMethods", config.processMethods));
        local.processMethodsParams = config.processMethodsParams;
        local.processMethodsResolv = config.processMethodsResolv;
        local.selector = params.get("selector", config.selector);
        return local;
    }
    //</editor-fold>
}
