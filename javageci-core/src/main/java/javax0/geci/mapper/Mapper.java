package javax0.geci.mapper;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.Format;
import javax0.jamal.api.BadSyntax;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * Code generator class that generates toMap and fromMap methods that will convert the object and possibly contained
 * other objects recursively (and also circular object references) into Map and back.
 * <p>
 * The {@code fromMap()} method creates a new object. To do that it uses the factory that can be configured
 * in the Geci annotation of the class, or else it just tries to use the default constructor of the class.
 *
 */
public class Mapper extends AbstractJavaGenerator {

    private static final String DEFAULTS = "!transient & !static";
    private final Class<? extends Annotation> generatedAnnotation;
    private final Function<String, String> field2MapKeyMapper;

    /**
     * Default constructor that creates a mapper that uses the default 'generated' annotation and the field names
     * are used as they are as keys in the map.
     */
    public Mapper() {
        generatedAnnotation = javax0.geci.annotations.Generated.class;
        field2MapKeyMapper = s -> s;
    }

    /**
     * Constructor with which you can specify the annotation used to decorate generated methods. The field names
     * are used as they are as keys in the map.
     *
     * @param generatedAnnotation the annotation to be used to decorate the generated methods.
     */
    public Mapper(Class<? extends Annotation> generatedAnnotation) {
        this.generatedAnnotation = generatedAnnotation;
        field2MapKeyMapper = s -> s;
    }

    /**
     * Create a new mapper object specifying the annotation to decorate the generated methods and also a field name
     * mapper.
     *
     * @param generatedAnnotation the annotation to be used to decorate the generated methods.
     * @param field2MapKeyMapper  the function used to calculate the name of the key in the Map for each field name
     */
    public Mapper(Class<? extends Annotation> generatedAnnotation, Function<String, String> field2MapKeyMapper) {
        this.generatedAnnotation = generatedAnnotation;
        this.field2MapKeyMapper = field2MapKeyMapper;
    }

    /**
     * Create a new mapper object specifying the field name to map key mapper. The annotation used to decorate the
     * generated methods is the default.
     *
     * @param field2MapKeyMapper the function used to calculate the name of the key in the Map for each field name
     */
    public Mapper(Function<String, String> field2MapKeyMapper) {
        this.generatedAnnotation = javax0.geci.annotations.Generated.class;
        this.field2MapKeyMapper = field2MapKeyMapper;
    }

    @Override
    public String mnemonic() {
        return "mapper";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var gid = global.get("id");
        var segment = source.open(gid);
        generateToMap(source, klass, global);
        generateFromMap(source, klass, global);

        final var factory = global.get("factory", "new {{class}}()");
        final var placeHolders = Map.of(
                "mnemonic", mnemonic(),
                "generatedBy", generatedAnnotation.getCanonicalName(),
                "class", klass.getSimpleName(),
                "factory", factory,
                "Map", "java.util.Map",
                "HashMap", "java.util.HashMap"
        );
        final var rawContent = segment.getContent();
        try {
            segment.setContent(Format.format(rawContent, placeHolders));
        } catch (BadSyntax badSyntax) {
            throw new IOException(badSyntax);
        }
    }

    private void generateToMap(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var fields = GeciReflectionTools.getAllFieldsSorted(klass);
        final var gid = global.get("id");
        var segment = source.open(gid);
        segment.write_r(getResourceString("tomap.jam"));
        for (final var field : fields) {
            final var local = GeciReflectionTools.getParameters(field, mnemonic());
            final var params = new CompoundParams(local, global);
            final var filter = params.get("filter", DEFAULTS);
            if (Selector.compile(filter).match(field)) {
                final var name = field.getName();
                if (hasToMap(field.getType())) {
                    segment.write("map.put(\"%s\", %s == null ? null : %s.toMap0(cache));", field2MapKey(name), name, name);
                } else {
                    segment.write("map.put(\"%s\",%s);", field2MapKey(name), name);
                }
            }
        }
        segment.write("return map;")
                ._l("}\n\n");
    }

    private String getResourceString(String resource) throws IOException {
        return new String(getClass().getResourceAsStream(resource).readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("\r", "");
    }

    private String field2MapKey(String name) {
        return field2MapKeyMapper.apply(name);
    }

    private boolean hasToMap(Class<?> type) {
        try {
            type.getDeclaredMethod("toMap");
            type.getDeclaredMethod("toMap0", Map.class);
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

        segment.write_r(getResourceString("frommap.jam"));
        for (final var field : fields) {
            final var local = GeciReflectionTools.getParameters(field, mnemonic());
            final var params = new CompoundParams(local, global);
            final var filter = params.get("filter", DEFAULTS);
            if (Selector.compile(filter).match(field)) {
                final var name = field.getName();
                if (hasFromMap(field.getType())) {
                    segment.write("it.%s = %s.fromMap0(({{Map}}<String,Object>)map.get(\"%s\"),cache);",
                            name,
                            field.getType().getCanonicalName(),
                            field2MapKey(name));
                } else {
                    segment.write("it.%s = (%s)map.get(\"%s\");",
                            name,
                            field.getType().getCanonicalName(),
                            field2MapKey(name));
                }
            }
        }
        segment.write("return it;\n")._l("}\n\n");
    }
}
