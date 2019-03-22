package javax0.geci.mapper;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

public class Mapper extends AbstractGenerator {

    private static final String DEFAULTS = "!transient & !static";
    private final Class<? extends Annotation> generatedAnnotation;

    public Mapper() {
        generatedAnnotation = javax0.geci.annotations.Generated.class;
    }

    public Mapper(Class<? extends Annotation> generatedAnnotation) {
        this.generatedAnnotation = generatedAnnotation;
    }

    @Override
    public String mnemonic() {
        return "mapper";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var gid = global.get("id");
        source.init(gid);
        generateToMap(source, klass, global);
        generateFromMap(source, klass, global);
    }

    private void generateToMap(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var fields = GeciReflectionTools.getAllFieldsSorted(klass);
        final var gid = global.get("id");
        var segment = source.open(gid);
        segment.write("@" + generatedAnnotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        segment.write_r("public java.util.Map<String,Object> toMap() {");
        segment.write("return toMap0( new java.util.IdentityHashMap<>());");
        segment.write_l("}");
        segment.write("@" + generatedAnnotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        segment.write_r("public java.util.Map<String,Object> toMap0(java.util.Map<Object, java.util.Map> cache) {");
        segment.write_r("if( cache.containsKey(this) ){");
        segment.write("return cache.get(this);");
        segment.write_l("}");
        segment.write("final java.util.Map<String,Object> map = new java.util.HashMap<>();");
        segment.write("cache.put(this,map);");
        for (final var field : fields) {
            final var local = GeciReflectionTools.getParameters(field, mnemonic());
            final var params = new CompoundParams(local, global);
            final var filter = params.get("filter", DEFAULTS);
            if (Selector.compile(filter).match(field)) {
                final var name = field.getName();
                if (hasToMap(field.getType())) {
                    segment.write("map.put(\"%s\", %s == null ? null : %s.toMap0(cache));", name, name, name);
                } else {
                    segment.write("map.put(\"%s\",%s);", name, name);
                }
            }
        }
        segment.write("return map;");
        segment.write_l("}");
        segment.newline();
    }

    private boolean hasToMap(Class<?> type) {
        try {
            type.getDeclaredMethod("toMap");
            type.getDeclaredMethod("toMap0",Map.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private boolean hasFromMap(Class<?> type) {
        try {
            type.getDeclaredMethod("fromMap", Map.class);
            type.getDeclaredMethod("fromMap0", Map.class, Map.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }

    }

    private void generateFromMap(Source source, Class<?> klass, CompoundParams global) throws IOException {
        final var fields = GeciReflectionTools.getAllFieldsSorted(klass);
        final var gid = global.get("id");
        var segment = source.open(gid);
        segment.write("@" + generatedAnnotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        segment.write_r("public static %s fromMap(java.util.Map map) {", klass.getSimpleName());
        segment.write("return fromMap0(map,new java.util.IdentityHashMap<>());");
        segment.write_l("}");
        segment.write_r("public static %s fromMap0(java.util.Map map,java.util.Map<java.util.Map,Object> cache) {", klass.getSimpleName());
        segment.write_r("if( cache.containsKey(map)){");
        segment.write("return (%s)cache.get(map);",klass.getSimpleName());
        segment.write_l("}");
        final var factory = global.get("factory", "new " + klass.getSimpleName() + "()");
        segment.write("final %s it = %s;", klass.getSimpleName(), factory);
        segment.write("cache.put(map,it);");
        for (final var field : fields) {
            final var local = GeciReflectionTools.getParameters(field, mnemonic());
            final var params = new CompoundParams(local, global);
            final var filter = params.get("filter", DEFAULTS);
            if (Selector.compile(filter).match(field)) {
                final var name = field.getName();
                if (hasFromMap(field.getType())) {
                    segment.write("it.%s = %s.fromMap0((java.util.Map<String,Object>)map.get(\"%s\"),cache);",
                            name,
                            field.getType().getCanonicalName(),
                            name);
                } else {
                    segment.write("it.%s = (%s)map.get(\"%s\");",
                            name,
                            field.getType().getCanonicalName(),
                            name);
                }
            }
        }
        segment.write("return it;");
        segment.write_l("}");
        segment.newline();
    }
}
