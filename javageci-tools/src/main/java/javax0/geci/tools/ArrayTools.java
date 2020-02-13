package javax0.geci.tools;

import java.util.Arrays;

public class ArrayTools {

    /**
     * Join the two arrays and return a new array that contains the elements of the array {@code a} and then the
     * elements of the array {@code b}. If {@code a} is {@code null} then the method returns {@code b}. If {@code a}
     * is not {@code null} and {@code b} is {@code null} then the method returns {@code a}. Otherwise it allocates a
     * new array and copies the values from the source arrays.
     *
     * @param a the first array to join
     * @param b the second array to append after the first one
     * @param <T> the type of the ararys
     * @return the joined two arrays or if any of the arguments is {@code null} then the other argument. Eventually
     * it returns {@code null} if both arguments are {@code null}
     */
    public static <T> T[] join(T[] a, T[] b) {
        if (a == null) {
            return b;
        } else {
            if (b == null) {
                return a;
            }
            final var array = Arrays.copyOf(a, a.length + b.length);
            System.arraycopy(b, 0, array, a.length, b.length);
            return array;
        }
    }
}
