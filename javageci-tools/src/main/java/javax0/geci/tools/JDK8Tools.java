package javax0.geci.tools;

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

}
