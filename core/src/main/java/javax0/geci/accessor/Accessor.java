package javax0.geci.accessor;

import javax0.geci.api.Generator;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Accessor implements Generator {
    private static final int PACKAGE = Modifier.PROTECTED | Modifier.PRIVATE | Modifier.PUBLIC;

    @Override
    public void process(Source source) {
        try {
            process0(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void process0(Source source) throws Exception {
        final var klass = source.getKlass();
        if (klass != null) {

            var global = Tools.getParameters(klass, "accessor");
            if (global != null) {
                var accessMask = mask(global.get("include"));
                var gid = global.get("id");
                source.init(gid);
                final var fields = klass.getDeclaredFields();
                for (final var field : fields) {
                    var local = Tools.getParameters(field, "accessor");
                    var params = new CompoundParams(local, global);
                    var isFinal = Modifier.isFinal(field.getModifiers());
                    if (params.isNot("exclude")) {
                        var name = nameAsString(field);
                        var ucName = cap(name);
                        var type = Tools.typeAsString(field);
                        var access = checkAccess(params.get("access", "public"));
                        var only = params.get("only");
                        if (matchMask(field, accessMask)) {
                            var id = getId(field, params);
                            source.init(id);
                            try (var segment = source.open(id)) {
                                if (!isFinal && !only.equals("setter")) {
                                    writeSetter(name, ucName, type, access, segment);
                                }
                                if (!"getter".equals(only)) {
                                    writeGetter(name, ucName, type, access, segment);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void writeGetter(String name, String ucName, String type, String access, Segment segment) {
        segment.write_r(access + " " + type + " get" + ucName + "(){");
        segment.write_l("return " + name + ";");
        segment.write("}");
        segment.newline();
    }

    private void writeSetter(String name, String ucName, String type, String access, Segment segment) {
        segment.write_r(access + " void set" + ucName + "(" +
            type + " " + name + "){");
        segment.write_l("this." + name + " = " + name + ";");
        segment.write("}");
        segment.newline();
    }

    private String checkAccess(String access) {
        if (access.equals("public") ||
            access.equals("protected") ||
            access.equals("private")
            ) {
            return access;
        }
        if (access.equals("package")) {
            return "";
        }
        throw new RuntimeException("Invalid acccess for setter/getter " + access);

    }

    private boolean matchMask(Field field, int mask) {
        int modifiers = field.getModifiers();
        if ((mask & Modifier.STRICT) != 0 && (modifiers & PACKAGE) == 0) {
            return true;
        }
        return (modifiers & mask) != 0;
    }

    private int mask(String includes) {
        int modMask = 0;
        if (includes == null) {
            modMask = Modifier.PRIVATE;
        } else {
            for (var exclude : includes.split(",")) {
                if (exclude.trim().equals("private")) {
                    modMask |= Modifier.PRIVATE;
                }
                if (exclude.trim().equals("public")) {
                    modMask |= Modifier.PUBLIC;
                }
                if (exclude.trim().equals("protected")) {
                    modMask |= Modifier.PROTECTED;
                }
                if (exclude.trim().equals("static")) {
                    modMask |= Modifier.STATIC;
                }
                if (exclude.trim().equals("package")) {
                    modMask |= Modifier.STRICT;//reuse the bit
                }
            }
        }
        return modMask;
    }

    private String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String nameAsString(Field field) {
        return field.getName();
    }

    private String getId(Field field, CompoundParams fieldParams) {
        var id = fieldParams.get("id");
        if (id == null) {
            throw new RuntimeException("accessor field " + field + " has no segment id");
        }
        return id;
    }
}
