package javax0.geci.tests.record;

import javax0.geci.annotations.Geci;

import java.util.Map;

@Geci("record")
public final class RecordClass {
    final public Map<String, String> z;
    final int a;
    final double b;

    //<editor-fold id="record">
    public RecordClass(int a, double b, java.util.Map<String, String> z) {
        this.a = a;
        this.b = b;
        this.z = z;
    }

    public int getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public java.util.Map<String, String> getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(a, b, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordClass that = (RecordClass) o;
        return java.util.Objects.equals(that.a, a) && java.util.Objects.equals(that.b, b) && java.util.Objects.equals(that.z, z);
    }

    //</editor-fold>
}
