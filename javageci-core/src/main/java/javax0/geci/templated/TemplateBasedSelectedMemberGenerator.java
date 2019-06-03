package javax0.geci.templated;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.TemplateLoader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static javax0.geci.tools.TemplateLoader.getTemplateContent;

public class TemplateBasedSelectedMemberGenerator extends AbstractFilteredFieldsGenerator {

    private class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "!static & !final";
        private final boolean declaredOnly = true;
        private String mnemonic = "templated";

        private void setDeclaredOnly(boolean b) {
            TemplateBasedSelectedMemberGenerator.this.declaredOnly = b;
        }

        // templates
        private String preprocess = null;
        private String processField = null;
        private String processFields = null;
        private String postprocess = null;
    }



    @Override
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
        segment.write(getTemplateContent(local.preprocess));
    }

    @Override
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
        segment.write(getTemplateContent(local.processField));
        for( final var key : segment.paramKeySet() ){
            if( key.startsWith("field.")){
                segment.param(key,null);
            }
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global, Field[] fields, Segment segment) throws Exception {
        final var local = localConfig(global);
        segment.param(
            "n", "" + fields.length
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
        segment.write(getTemplateContent(local.processFields));
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) throws Exception {
        final var local = localConfig(global);
        segment.write(getTemplateContent(local.postprocess));
    }

    @Override
    public String mnemonic() {
        return config.mnemonic;
    }

    //<editor-fold id="configBuilder" generateImplementedKeys="false">
    private final Config config = new Config();
    public static TemplateBasedSelectedMemberGenerator.Builder builder() {
        return new TemplateBasedSelectedMemberGenerator().new Builder();
    }

    public class Builder {
        public Builder declaredOnly(boolean declaredOnly) {
            config.setDeclaredOnly(declaredOnly);
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generatedAnnotation(Class generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            config.mnemonic = mnemonic;
            return this;
        }

        public Builder postprocess(String postprocess) {
            config.postprocess = postprocess;
            return this;
        }

        public Builder preprocess(String preprocess) {
            config.preprocess = preprocess;
            return this;
        }

        public Builder processField(String processField) {
            config.processField = processField;
            return this;
        }

        public Builder processFields(String processFields) {
            config.processFields = processFields;
            return this;
        }

        public TemplateBasedSelectedMemberGenerator build() {
            return TemplateBasedSelectedMemberGenerator.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.setDeclaredOnly(config.declaredOnly);
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.mnemonic = params.get("mnemonic",config.mnemonic);
        local.postprocess = params.get("postprocess",config.postprocess);
        local.preprocess = params.get("preprocess",config.preprocess);
        local.processField = params.get("processField",config.processField);
        local.processFields = params.get("processFields",config.processFields);
        return local;
    }
    //</editor-fold>
}
