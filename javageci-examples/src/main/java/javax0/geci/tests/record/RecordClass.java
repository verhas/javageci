package javax0.geci.tests.record;

import javax0.geci.annotations.Geci;

import java.util.Map;


@Geci("record")
public final class RecordClass {
    final public Map<String, String> z;
    final int a;
    final double b;

    private void RecordClass(final java.util.Map<String,String> z, final int a, final double b){
        // do nothing, really
    }

    //<editor-fold id="record">
    public RecordClass(final java.util.Map<String,String> z, final int a, final double b) {
        RecordClass(z, a, b);
        this.z = z;
        this.a = a;
        this.b = b;
    }

    public java.util.Map<String,String> getZ() {
        return z;
    }

    public int getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(z, a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordClass that = (RecordClass) o;
        return java.util.Objects.equals(that.z, z) && java.util.Objects.equals(that.a, a) && java.util.Objects.equals(that.b, b);
    }
    //</editor-fold>
}
