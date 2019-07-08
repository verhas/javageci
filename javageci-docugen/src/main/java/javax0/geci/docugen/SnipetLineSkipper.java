package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='skip'")
public class SnipetLineSkipper extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        if (params.id().equals("remove")) {
            removeSkippers(snippet);
        } else {
            skipLines(snippet);
        }
    }

    private Pattern skip = Pattern.compile("skip");
    private Pattern skipEnd = Pattern.compile("skip\\s+end");
    private Pattern skipNrLines = Pattern.compile("skip\\s+(\\d+)\\s+lines?");
    private Pattern skipTill = Pattern.compile("skip\\s+till\\s+/(.*?)/");

    private void removeSkippers(Snippet snippet) {
        final var modifiedLines = new ArrayList<String>();
        for (final var line : snippet.lines()) {
            if (!skip.matcher(line).find()) {
                modifiedLines.add(line);
            }
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private void skipLines(Snippet snippet) {
        final var modifiedLines = new ArrayList<String>();
        var skipping = false;
        int skipCounter=0;
        Pattern skipPattern = null;
        for (final var line : snippet.lines()) {
            if (skipping) {
                if (skipCounter > 0) {
                    skipCounter--;
                    skipping = skipCounter > 0 ;
                    skipPattern = null;
                    continue;
                }
                if( skipPattern != null && skipPattern.matcher(line).find()){
                    modifiedLines.add(line);
                    skipping = false;
                    skipPattern = null;
                    skipCounter = 0;
                    continue;
                }
                if( skipEnd.matcher(line).find()){
                    skipping = false;
                    skipPattern = null;
                    skipCounter = 0;
                    continue;
                }
            } else {
                final var skipNrLinesMatcher = skipNrLines.matcher(line);
                if (skipNrLinesMatcher.find()) {
                    skipCounter = Integer.parseInt(skipNrLinesMatcher.group(1));
                    skipPattern = null;
                    skipping = true;
                    continue;
                }
                final var skipTillMatcher = skipTill.matcher(line);
                if (skipTillMatcher.find()) {
                    skipPattern = Pattern.compile(skipTillMatcher.group(1));
                    skipCounter = 0;
                    skipping = true;
                    continue;
                }
                final var skipMatcher = skip.matcher(line);
                if (skipMatcher.find()) {
                    skipPattern = null;
                    skipCounter = 0;
                    skipping = true;
                    continue;
                }
                modifiedLines.add(line);
            }
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "skip";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static SnipetLineSkipper.Builder builder() {
        return new SnipetLineSkipper().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnipetLineSkipper build() {
            return SnipetLineSkipper.this;
        }
    }
    //</editor-fold>
}
