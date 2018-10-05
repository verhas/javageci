package javax0.geci.delegator;

import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.tools.Tools;

import java.lang.reflect.Modifier;

public class Delegator implements Generator {
    @Override
    public void process(Source source) {
        try {
            process0(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void process0(Source source) throws Exception {
        final var klass = Tools.getClass(source);
        if (klass != null) {
            final var fields = klass.getDeclaredFields();
            for (final var field : fields) {
                var params = Tools.getParameters(field, "delegate");
                if (params != null) {
                    if (!params.containsKey("id")) {
                        throw new RuntimeException("Field " + field + " in class " + klass + " has no 'id' in annotation Geci");
                    }
                    var id = params.get("id");
                    var delClass = field.getType();
                    var methods = delClass.getDeclaredMethods();
                    var name = field.getName();
                    try (var segment = source.open(id)) {
                        for (var method : methods) {
                            var modifiers = method.getModifiers();
                            if (Modifier.isPublic(modifiers)) {
                                segment.write_r("" + Tools.modifiersString(method) +
                                        Tools.typeAsString(method) +
                                        " " + method.getName() + "(" +


                                        "){");
                                if (!"void".equals(method.getReturnType().getName())) {
                                        segment.write_l("return "+name+"."+method.getName()+"();");
                                }else{
                                        segment.write_l(name+"."+method.getName()+"();");
                                }
                                segment.write("}");
                            }
                        }
                    }
                }
            }
        }
    }
}
