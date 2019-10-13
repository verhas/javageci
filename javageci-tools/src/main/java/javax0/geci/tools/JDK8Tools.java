package javax0.geci.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Various static methods that implement functionalities that are available only in JDK8+ and thus cannot be used when
 * targeting JDK8 code.
 */
public class JDK8Tools {

    public static String space(int n){
        final StringBuilder sb = new StringBuilder(/*20 spaces*/"                    ");
        while( sb.length() < n){
            sb.append(sb);
        }
        return sb.substring(0,n).toString();
    }

    private static final Method getNestHostMethod;

    static {
        Method _getNestHost = null;
        try {
            _getNestHost = Class.class.getDeclaredMethod("getNestHost");
        } catch (NoSuchMethodException e) {
        }
        getNestHostMethod = _getNestHost;
    }

    public static Class<?> getNestHost(Class<?> klass) {
        if (getNestHostMethod == null) {
            return jdk8_getNestHost(klass);
        } else {
            try {
                return (Class) getNestHostMethod.invoke(klass);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return klass;
            }
        }
    }

    static Class<?> jdk8_getNestHost(Class<?> klass) {
        if (klass.isMemberClass() ) {
            final var dollarIndex = klass.getName().indexOf("$");
            if (dollarIndex == -1) {
                return klass;
            }
            try {
                return Class.forName(klass.getName().substring(0, dollarIndex));
            } catch (ClassNotFoundException e) {
                return klass;
            }
        } else {
            return klass;
        }
    }

    public static <K,V> Map<K,V> asMap(Object ... objects){
        if( objects.length %2 == 1 ){
            throw new IllegalArgumentException("asMap must have even number of parameters");
        }
        final var map = new HashMap<K,V>(objects.length / 2);
        for( int i = 0 ; i < objects.length ; i += 2 ){
            map.put((K)objects[i],(V)objects[i+1]);
        }
        return map;
    }

    /**
     * This is practically the copy from JDK9.
     * @param klass the class of which we calculate the package name
     * @return the package name of the class
     */
    public static String getPackageName(Class<?> klass) {
        String pn;
        Class<?> c = klass;
        while (c.isArray()) {
            c = c.getComponentType();
        }
        if (c.isPrimitive()) {
            pn = "java.lang";
        } else {
            String cn = c.getName();
            int dot = cn.lastIndexOf('.');
            pn = (dot != -1) ? cn.substring(0, dot).intern() : "";
        }
        return pn;
    }

    public static String stripLeading(final String s){
        return s.replaceAll("^\\s*","");
    }

    public static String stripTrailing(final String s){
        return s.replaceAll("\\s*$","");
    }

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static byte[] readAllBytes(final InputStream is) throws IOException {
        return readNBytes(is,Integer.MAX_VALUE);
    }

    public static byte[] readNBytes(final InputStream is, final int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = is.read(buf, nread,
                Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }
}
