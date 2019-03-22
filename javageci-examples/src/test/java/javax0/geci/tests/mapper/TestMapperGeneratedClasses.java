package javax0.geci.tests.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class TestMapperGeneratedClasses {

    @Test
    @DisplayName("Test a freshly initialized object having even null pointers")
    void mapsEmpty() {
        Person p = new Person();
        Map<String, Object> mapped = p.toMap();
        Assertions.assertEquals(5, mapped.size());
        Assertions.assertNull(mapped.get("mother"));
        Assertions.assertFalse((Boolean) mapped.get("willMapPublicInherited"));
    }

    @Test
    @DisplayName("Test a freshly initialized object referencing another")
    void mapsSimple() throws NoSuchFieldException, IllegalAccessException {
        Person boy = new Person();
        Person mother = new Person();
        Field f = Person.class.getDeclaredField("mother");
        f.setAccessible(true);
        f.set(boy, mother);
        Map<String, Object> mapped = boy.toMap();
        Assertions.assertEquals(5, ((Map) mapped.get("mother")).size());
        Assertions.assertNotNull(mapped.get("mother"));
        Assertions.assertFalse((Boolean) mapped.get("willMapPublicInherited"));
    }

    @Test
    @DisplayName("Test a freshly initialized object referencing itself")
    void mapsRecursive() throws NoSuchFieldException, IllegalAccessException {
        Person boy = new Person();
        Field f = Person.class.getDeclaredField("mother");
        f.setAccessible(true);
        f.set(boy, boy);
        Map<String, Object> mapped = boy.toMap();
        Assertions.assertEquals(5, ((Map) mapped.get("mother")).size());
        Assertions.assertSame(mapped, mapped.get("mother"));
        Assertions.assertFalse((Boolean) mapped.get("willMapPublicInherited"));
    }

    @Test
    @DisplayName("Test a freshly initialized object referencing itself mapped and demapped")
    void mapsAndDemapsRecursive() throws NoSuchFieldException, IllegalAccessException {
        Person boy = new Person();
        Field f = Person.class.getDeclaredField("mother");
        f.setAccessible(true);
        f.set(boy, boy);
        Map<String, Object> mapped = boy.toMap();
        Person p = Person.fromMap(mapped);
        Assertions.assertEquals(p, f.get(p));
    }
}
