package javax0.geci.delegator;

import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.tools.Tools;

public class Delegate implements Generator {
    @Override
    public void process(Source source) {
        try {
            process0(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void process0(Source source) throws Exception {
        final Class klass;
        klass = Tools.getClass(source);
        var fields = klass.getDeclaredFields();
        for (var field : fields) {
            var params = Tools.getParameters(field, "delegate");
            if (!params.containsKey("id")) {
                throw new RuntimeException("Field " + field + " in class " + klass + " has no 'id' in annotation Geci");
            }
            var id = params.get("id");
            var segment = source.open(id);
            var delClass = field.getType();
            var methods = delClass.getMethods();
            var name = field.getName();
            for( var method : methods ){
                segment.write_r(""+method.getModifiers()+" "+method.getName()+"(){");
                segment.write_l("}");
            }
        }
    }
}
