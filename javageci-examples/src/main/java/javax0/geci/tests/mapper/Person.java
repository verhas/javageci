package javax0.geci.tests.mapper;


import javax0.geci.annotations.Geci;

import java.util.HashMap;

/**
 * Sample
 */
@Geci("mapper factory='newPerson()'")
public class Person extends AbstractPerson {
    private HashMap<String, String> willMapPrivateHashMap;
    private Person mother;
    @Geci("mapper filter='false'")
    private Person father;

    private static Person newPerson() {
        return new Person();
    }

    //<editor-fold id="mapper">
    @javax0.geci.annotations.Generated("mapper")
    public java.util.Map<String,Object> toMap() {
        final java.util.Map<String,Object> map = new HashMap<>();
        map.put("mother",mother.toMap());
        map.put("willMapDefaultInherited",willMapDefaultInherited);
        map.put("willMapPrivateHashMap",willMapPrivateHashMap);
        map.put("willMapProtectedInherited",willMapProtectedInherited);
        map.put("willMapPublicInherited",willMapPublicInherited);
        return map;
    }

    @javax0.geci.annotations.Generated("mapper")
    public static Person fromMap(java.util.Map map) {
        final Person it = newPerson();
        it.mother = javax0.geci.tests.mapper.Person.fromMap((java.util.Map<String,Object>)map.get("mother"));
        it.willMapDefaultInherited = (float)map.get("willMapDefaultInherited");
        it.willMapPrivateHashMap = (java.util.HashMap)map.get("willMapPrivateHashMap");
        it.willMapProtectedInherited = (int)map.get("willMapProtectedInherited");
        it.willMapPublicInherited = (boolean)map.get("willMapPublicInherited");
        return it;
    }

    //</editor-fold>
}
