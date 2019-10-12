package javax0.geci.tests.record;

import javax0.geci.annotations.Geci;

@Geci("record")
public final class Range {
    final  int  lo;
    final  int  hi;

     public void Range(final int lo, final int hi) {
        if (lo > hi)  /* referring here to the implicit constructor parameters */
            throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
    }

    //<editor-fold id="record">
    public Range(final int lo, final int hi) {
        Range(lo, hi);
        this.lo = lo;
        this.hi = hi;
    }

    public int getLo() {
        return lo;
    }

    public int getHi() {
        return hi;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(lo, hi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range that = (Range) o;
        return java.util.Objects.equals(that.lo, lo) && java.util.Objects.equals(that.hi, hi);
    }
    //</editor-fold>
}
