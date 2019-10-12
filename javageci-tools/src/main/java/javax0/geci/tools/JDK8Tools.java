package javax0.geci.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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

}
