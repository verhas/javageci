package javax0.geci.tools;

import java.util.Map;

public class Template {
    final Map<String, String> params;

    public Template(Map<String, String> params) {
        this.params = params;
    }

    public String resolve(String s) {
        final StringBuilder sb = new StringBuilder(s);
        int position = 0;
        int start;
        while ((start = sb.indexOf("{{", position)) > 0) {
            int end = sb.indexOf("}}",start);
            if( end > 0 ) {
                final var key = sb.substring(start + 2, end);
                if( params.containsKey(key)){
                    final var value = params.get(key);
                    sb.replace(start,end+2,value);
                    position = start + value.length();
                }else{
                    position = end + 2;
                }
            }else{
                break;
            }
        }
        return sb.toString();
    }
}
