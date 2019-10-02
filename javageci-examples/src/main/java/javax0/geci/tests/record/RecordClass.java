package javax0.geci.tests.record;

import javax0.geci.annotations.Geci;

import java.util.Map;

@Geci("record")
public final class RecordClass {
    final public Map<String, String> z;
    final int a;
    final double b;

    //<editor-fold id="record">
    public RecordClass(
        int a, double b, java.util.Map<String, String> z
    ) {
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
    //</editor-fold>
}
