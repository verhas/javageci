package javax0.geci.tests.mapper;


import javax0.geci.annotations.Geci;

import java.util.HashMap;

/**
 * Sample
 */
public class Person extends AbstractPerson {
    private HashMap<String, String> willMapPrivateHashMap;
    private Person mother;
    @Geci("mapper filter='false'")
    private Person father;

    private static Person newPerson() {
        return new Person();
    }

    //<editor-fold id="equals" useSuper="ok">
    @javax0.geci.annotations.Generated("equals")
    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + (father != null ? father.hashCode() : 0);
        result = 31 * result + (mother != null ? mother.hashCode() : 0);
        result = 31 * result + (willMapPrivateHashMap != null ? willMapPrivateHashMap.hashCode() : 0);
        return result;
    }
    @javax0.geci.annotations.Generated("equals")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        if (o == null || getClass() != o.getClass()) return false;

        Person that = (Person) o;
        if (father != null ? !father.equals(that.father) : that.father != null) return false;
        if (mother != null ? !mother.equals(that.mother) : that.mother != null) return false;
        return willMapPrivateHashMap != null ? willMapPrivateHashMap.equals(that.willMapPrivateHashMap) : that.willMapPrivateHashMap == null;
    }

    //</editor-fold>

    //<editor-fold id="mapper" factory="newPerson()">
    @javax0.geci.annotations.Generated("mapper")
    public java.util.Map<String,Object> toMap() {
        return toMap0( new java.util.IdentityHashMap<>());
    }
    @javax0.geci.annotations.Generated("mapper")
    public java.util.Map<String,Object> toMap0(java.util.Map<Object, java.util.Map> cache) {
        if( cache.containsKey(this) ){
            return cache.get(this);
        }
        final java.util.Map<String,Object> map = new java.util.HashMap<>();
        cache.put(this,map);
        map.put("mother", mother == null ? null : mother.toMap0(cache));
        map.put("willMapDefaultInherited",willMapDefaultInherited);
        map.put("willMapPrivateHashMap",willMapPrivateHashMap);
        map.put("willMapProtectedInherited",willMapProtectedInherited);
        map.put("willMapPublicInherited",willMapPublicInherited);
        return map;
    }
    @javax0.geci.annotations.Generated("mapper")
    public static Person fromMap(java.util.Map map) {
        return fromMap0(map,new java.util.IdentityHashMap<>());
    }
    public static Person fromMap0(java.util.Map map,java.util.Map<java.util.Map,Object> cache) {
        if( cache.containsKey(map)){
            return (Person)cache.get(map);
        }
        final Person it = newPerson();
        cache.put(map,it);
        it.mother = javax0.geci.tests.mapper.Person.fromMap0((java.util.Map<String,Object>)map.get("mother"),cache);
        it.willMapDefaultInherited = (float)map.get("willMapDefaultInherited");
        it.willMapPrivateHashMap = (java.util.HashMap)map.get("willMapPrivateHashMap");
        it.willMapProtectedInherited = (int)map.get("willMapProtectedInherited");
        it.willMapPublicInherited = (boolean)map.get("willMapPublicInherited");
        return it;
    }
    //</editor-fold>
}
