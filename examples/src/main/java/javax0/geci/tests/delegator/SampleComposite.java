package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

import java.util.Map;

@Geci("delegator")
public class SampleComposite<K,V> implements Map<K,V>{
    @Geci("delegator id='contained1'")
    Map<K,V> contained1;

    // <editor-fold id="contained1" desc="delegated methods to contained1">
    @javax0.geci.annotations.Generated("delegator")
    public V compute(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained1.compute(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfAbsent(K arg1, java.util.function.Function<? super K,? extends V> arg2) {
        return contained1.computeIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfPresent(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained1.computeIfPresent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V get(Object arg1) {
        return contained1.get(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V getOrDefault(Object arg1, V arg2) {
        return contained1.getOrDefault(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V merge(K arg1, V arg2, java.util.function.BiFunction<? super V,? super V,? extends V> arg3) {
        return contained1.merge(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V put(K arg1, V arg2) {
        return contained1.put(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V putIfAbsent(K arg1, V arg2) {
        return contained1.putIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V remove(Object arg1) {
        return contained1.remove(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V replace(K arg1, V arg2) {
        return contained1.replace(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsKey(Object arg1) {
        return contained1.containsKey(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsValue(Object arg1) {
        return contained1.containsValue(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean equals(Object arg1) {
        return contained1.equals(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean isEmpty() {
        return contained1.isEmpty();
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean remove(Object arg1, Object arg2) {
        return contained1.remove(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean replace(K arg1, V arg2, V arg3) {
        return contained1.replace(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public int hashCode() {
        return contained1.hashCode();
    }

    @javax0.geci.annotations.Generated("delegator")
    public int size() {
        return contained1.size();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Collection<V> values() {
        return contained1.values();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<K> keySet() {
        return contained1.keySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<java.util.Map.Entry<K,V>> entrySet() {
        return contained1.entrySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void clear() {
        contained1.clear();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void forEach(java.util.function.BiConsumer<? super K,? super V> arg1) {
        contained1.forEach(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void putAll(java.util.Map<? extends K,? extends V> arg1) {
        contained1.putAll(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void replaceAll(java.util.function.BiFunction<? super K,? super V,? extends V> arg1) {
        contained1.replaceAll(arg1);
    }

    // </editor-fold>

}
