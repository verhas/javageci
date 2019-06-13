package javax0.geci.repeated;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repeated extends AbstractJavaGenerator {
    private class Config {
        private String start = ".*//\\s*START\\s*";
        private String matchLine = "(.*)";
        private String end = ".*//\\s*END\\s*";
        private String values = null;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var local = localConfig(global);
        final var startPattern = Pattern.compile(local.start);
        final var matchLinePattern = Pattern.compile(local.matchLine);
        final var endPattern = Pattern.compile(local.end);
        boolean switchOn = false;
        final var loopVars = new ArrayList<String>();
        for (final var line : source.getLines()) {
            if (startPattern.matcher(line).matches()) {
                switchOn = true;
                continue;
            }
            if (endPattern.matcher(line).matches()) {
                switchOn = false;
                continue;
            }
            if (switchOn) {
                final Matcher matcher = matchLinePattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.groupCount() != 1) {
                        throw new GeciException("matchLine does not contain any group between ( and )");
                    }
                    loopVars.add(matcher.group(1));
                }
            }
        }
        if (local.values != null) {
            loopVars.addAll(List.of(local.values.split(",")));
        }
        Segment segment = source.open(global.id());
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public static Repeated.Builder builder() {
        return new Repeated().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
            "end",
            "matchLine",
            "start",
            "values",
            "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }

    public class Builder {
        public Builder end(String end) {
            config.end = end;
            return this;
        }

        public Builder matchLine(String matchLine) {
            config.matchLine = matchLine;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder values(String values) {
            config.values = values;
            return this;
        }

        public Repeated build() {
            return Repeated.this;
        }
    }

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.end = params.get("end", config.end);
        local.matchLine = params.get("matchLine", config.matchLine);
        local.start = params.get("start", config.start);
        local.values = params.get("values", config.values);
        return local;
    }
    //</editor-fold>
}
