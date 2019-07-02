package javax0.geci.docugen;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Snippet {
    final private Map<String,String> params;
    final private List<String> lines;

    public Snippet(Map<String, String> params, List<String> lines) {
        this.params = params;
        this.lines = lines;
    }

    public String param(String s){
        return params.get(s);
    }
    public Set<String> keys(){
        return params.keySet();
    }
}
