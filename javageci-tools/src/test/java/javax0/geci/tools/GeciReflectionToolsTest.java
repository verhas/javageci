package javax0.geci.tools;

import javax0.geci.api.Logger;
import javax0.geci.api.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GeciReflectionToolsTest {
    @javax0.geci.annotations.Geci("aaa a='b' b='c' c='d' a$='dollared' b3='bthree' _='-'")
    @javax0.geci.annotations.Geci("xxx x='x' y='y' z='z'")
    private static Object something;
    private HashMap<Map<String, Integer>, Object> b;

    private static java.util.Map.Entry<String, Integer>[] m1() {
        return null;
    }

    private static java.util.Map.Entry<? extends String, ? super Integer>[] m2() {
        return null;
    }

    @Test
    void testParameterParser() {
        var map = GeciAnnotationTools.getParameters("a='b' b='c' c='d'");
        assertEquals(map.size(), 3);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
    }

    @Test
    void testParameterFetcher() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("something");
        var map = GeciReflectionTools.getParameters(f, "aaa");
        assertNotNull(map);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
        assertEquals(map.get("a$"), "dollared");
        assertEquals(map.get("b3"), "bthree");
        assertEquals(map.get("_"), "-");

    }

    @Test
    void testTypeGetting() throws NoSuchMethodException, NoSuchFieldException {
        assertEquals(
                "void",
                GeciReflectionTools.typeAsString(this.getClass().getDeclaredMethod("testTypeGetting")));
        assertEquals(
                "java.util.HashMap<java.util.Map<String,Integer>,Object>",
                GeciReflectionTools.typeAsString(this.getClass().getDeclaredField("b")));
    }

    @Test
    void normalizesGenericNames() {
        Assertions.assertAll(
                () -> assertEquals("String", GeciReflectionTools.normalizeTypeName("java.lang.String")),
                () -> assertEquals("java.util.Map", GeciReflectionTools.normalizeTypeName("java.util.Map")),
                () -> assertEquals("java.util.Map<Integer,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.lang.Integer,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set<java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set< java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<com.java.lang.Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set< com. java.lang.Integer>,java.lang.String>")),
                () -> assertEquals("java.util.Map<java.util.Set<? extends com.java.lang.Integer>,String>",
                        GeciReflectionTools.normalizeTypeName("java.util.Map<java.util.Set<? extends    com. java.lang.Integer> , java.lang.String>"))
        );
    }

    @Test
    void normalizeType() {
        Assertions.assertAll(
                () -> assertEquals("java.util.Set<java.util.Map.Entry<K,V>>", GeciReflectionTools.getGenericTypeName(Map.class.getDeclaredMethod("entrySet").getGenericReturnType())),
                () -> assertEquals("java.util.Map.Entry", GeciReflectionTools.getGenericTypeName(java.util.Map.Entry.class)),
                () -> assertEquals("java.util.Map.Entry<String,Integer>[]", GeciReflectionTools.getGenericTypeName(this.getClass().getDeclaredMethod("m1").getGenericReturnType())),
                () -> assertEquals("java.util.Map.Entry<? extends String,? super Integer>[]", GeciReflectionTools.getGenericTypeName(this.getClass().getDeclaredMethod("m2").getGenericReturnType()))
        );
    }


    private static class Z<H> {
        private static class U<T extends String>{}
    }

    @Test
    void getSimpleGenericName() {
        Assertions.assertAll(
                () -> assertEquals("Entry<K,V>", GeciReflectionTools.getSimpleGenericClassName(java.util.Map.Entry.class)),
                () -> assertEquals("Map.Entry<K,V>", GeciReflectionTools.getLocalGenericClassName(java.util.Map.Entry.class)),
                () -> assertEquals("U<T>", GeciReflectionTools.getSimpleGenericClassName(Z.U.class)),
                () -> assertEquals("GeciReflectionToolsTest.Z.U<T>", GeciReflectionTools.getLocalGenericClassName(Z.U.class))
        );
    }

    @Test
    void getParametersFromSource() {
        Source testSource = new AbstractTestSource() {
            @Override
            public List<String> getLines() {
                return List.of("    // @Geci(\"aaa a='b' b='c' c='d' a$='dollared' b3='bthree' _='-'\")",
                        "    // @Geci(\"xxx x='x' y='y' z='z'\")",
                        "    private static Object something;",
                        "    private HashMap<Map<String, Integer>, Object> b;");
            }
            @Override
            public Logger getLogger() {
                return null;
            }
        };

        var map = GeciAnnotationTools.getParameters(testSource, "aaa", "//", Pattern.compile(".*something;.*"));
        assertNotNull(map);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
        assertEquals(map.get("a$"), "dollared");
        assertEquals(map.get("b3"), "bthree");
        assertEquals(map.get("_"), "-");

    }


    @Test
    @DisplayName("Get the gecis from the standard annotations")
    @javax0.geci.annotations.Geci("barbarumba k1='v1' k2='v2'")
    void getGecisFromAnnotation() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromAnnotation"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k1='v1' k2='v2'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations")
    @GeciReflectionToolsTest.Geci("barbarumba k1='v1' k2='v2'")
    void getGecisFromOwnAnnotation() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotation"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k1='v1' k2='v2'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter")
    @GeciReflectionToolsTest.Geci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationParams() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationParams"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @GeciReflectionToolsTest.MyGeci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationMyNameParams() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyNameParams"));
        Assertions.assertEquals(1, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @GeciReflectionToolsTest.MyWrongGeci(value = "barbarumba k2='v2'", k1 = "v1")
    void getGecisFromOwnAnnotationMyWrongNameParams() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyWrongNameParams"));
        Assertions.assertEquals(0, gecis.length);
    }

    @Test
    @DisplayName("Get the gecis from own annotations with annotation parameter that is not named Geci")
    @GeciReflectionToolsTest.MyGeci(value = "barbarumba k2='v2'", k1 = "v1")
    @GeciReflectionToolsTest.MyGeci(k1 = "v1")
    void getGecisFromOwnAnnotationMyNameParamsMultiple() throws NoSuchMethodException {
        final var gecis = GeciAnnotationTools.getGecis(this.getClass().getDeclaredMethod("getGecisFromOwnAnnotationMyNameParamsMultiple"));
        Assertions.assertEquals(2, gecis.length);
        Assertions.assertEquals("barbarumba k2='v2' k1='v1'", gecis[0]);
        Assertions.assertEquals("myGeci k1='v1'", gecis[1]);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Geci {
        String value();

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Gecis {
        Geci[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(MyGecis.class)
    @Geci("")
    public @interface MyGeci {
        String value() default "";

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyWrongGeci {
        String value();

        String k1() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyGecis {
        MyGeci[] value();
    }
}
