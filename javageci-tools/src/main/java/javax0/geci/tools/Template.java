package javax0.geci.tools;

import java.util.Map;

/**
 * <p>A very simple Moustache like (light) templating. The method {@link #resolve(String)} replaces every
 * {{key}} string with the value as defined in the map passed as a construction parameter.</p>
 *
 * <p>Examples:</p>
 * <table><caption>. .</caption>
 *     <tr><th>Map values</th><th>input</th><th>output</th></tr>
 *     <!-- snip Template_javadoc snippet="epsilon"
 *           append="snippets='Template_test_.*'"
 *           regex="escape='~'
 *           replace='|Assertions.assertEquals~((\".*?\"),~s*sut~.resolve~((\".*?\")~)~);|<td>$2</td><td>$1</td></tr>|'
 *           replace='|final~s+var~s+sut~s+=~s+new~s+Template~(Map.of~((.*?)~)~);|<tr><td>$1</td>|'"
 *           -->
 *         <tr><td>"a","b", "huhh","spooky"</td>
 *             <td>"this is a {{huhh}} {{a}}a{{a}}oon"</td><td>"this is a spooky baboon"</td></tr>
 *         <tr><td>"a", "b", "huhh", "spooky"</td>
 *         <td>"{{a}}this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"</td><td>"bthis is {{a...}} spooky bab{{oon"</td></tr>
 *         <tr><td>"a","b", "huhh","spooky"</td>
 *         <td>"this is {{a...}} {{huhh}} {{a}}a{{a}}oon"</td><td>"this is {{a...}} spooky baboon"</td></tr>
 *         <tr><td>"a","b", "huhh","spooky"</td>
 *         <td>"this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"</td><td>"this is {{a...}} spooky bab{{oon"</td></tr>
 *     <!-- end snip -->
 * </table>
 *
 */
public class Template {
    final private Map<String, String> params;

    public Template(Map<String, String> params) {
        this.params = params;
    }

    public String resolve(String s) {
        final StringBuilder sb = new StringBuilder(s);
        int position = 0;
        int start;
        while ((start = sb.indexOf("{{", position)) >= 0) {
            int end = sb.indexOf("}}",start);
            if( end > 0 ) {
                final var key = sb.substring(start + 2, end);
                if( params.containsKey(key) && params.get(key) != null ){
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
