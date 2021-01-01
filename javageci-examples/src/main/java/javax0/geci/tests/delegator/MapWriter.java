package javax0.geci.tests.delegator;

import javax0.geci.annotations.Geci;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

// snippet MapWriter_head
@Geci("delegator")
public class MapWriter<K, V> extends Writer implements Map<K, V> {

    @Geci("delegator id='map' methods=' !name ~ /equals|hashCode/ & !static '")
    final Map<K, V> contained = new HashMap<>();
//end snippet

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    //snippet generated_map
    //<editor-fold id="map">
    @javax0.geci.annotations.Generated("delegator")
    public V compute(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained.compute(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfAbsent(K arg1, java.util.function.Function<? super K,? extends V> arg2) {
        return contained.computeIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V computeIfPresent(K arg1, java.util.function.BiFunction<? super K,? super V,? extends V> arg2) {
        return contained.computeIfPresent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V getOrDefault(Object arg1, V arg2) {
        return contained.getOrDefault(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V merge(K arg1, V arg2, java.util.function.BiFunction<? super V,? super V,? extends V> arg3) {
        return contained.merge(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V putIfAbsent(K arg1, V arg2) {
        return contained.putIfAbsent(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V replace(K arg1, V arg2) {
        return contained.replace(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V get(Object arg1) {
        return contained.get(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V put(K arg1, V arg2) {
        return contained.put(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public V remove(Object arg1) {
        return contained.remove(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsKey(Object arg1) {
        return contained.containsKey(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean containsValue(Object arg1) {
        return contained.containsValue(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean isEmpty() {
        return contained.isEmpty();
    }

    @javax0.geci.annotations.Generated("delegator")
    public int size() {
        return contained.size();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Collection<V> values() {
        return contained.values();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<K> keySet() {
        return contained.keySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public java.util.Set<java.util.Map.Entry<K,V>> entrySet() {
        return contained.entrySet();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void clear() {
        contained.clear();
    }

    @javax0.geci.annotations.Generated("delegator")
    public void putAll(java.util.Map<? extends K,? extends V> arg1) {
        contained.putAll(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean remove(Object arg1, Object arg2) {
        return contained.remove(arg1,arg2);
    }

    @javax0.geci.annotations.Generated("delegator")
    public boolean replace(K arg1, V arg2, V arg3) {
        return contained.replace(arg1,arg2,arg3);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void forEach(java.util.function.BiConsumer<? super K,? super V> arg1) {
        contained.forEach(arg1);
    }

    @javax0.geci.annotations.Generated("delegator")
    public void replaceAll(java.util.function.BiFunction<? super K,? super V,? extends V> arg1) {
        contained.replaceAll(arg1);
    }

    //</editor-fold>
    //end snippet
}
