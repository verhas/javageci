package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

@Geci("configBuilder localConfigMethod=''")
public class JavaDocSnippetInserter extends AbstractSnippeter implements NonConfigurable {

    private static class Config extends AbstractSnippeter.Config {
    }


    private JavaDocSnippetInserter(){
        new Builder().files("\\.java$");
    }

    @Override
    public void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        snippet.lines().forEach(line -> segment.write("* " + line));
    }

    /**
     * The mnemonic of this generator has to be null. If the mnemonic of
     * a snippet handling generator is not null then it is only invoked
     * from {@link AbstractSnippeter} if the mnemonic is configured in
     * the snippet header (it is used). When we insert snippet into the
     * source (markdown) we do not request it explicitly, but since the
     * mnemonic returned from here is {@code null} and could not match
     * any string in the header it is invoked.
     *
     * @return {@code null}
     */
    @Override
    public String mnemonic() {
        return null;
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();

    public static JavaDocSnippetInserter.Builder builder() {
        return new JavaDocSnippetInserter().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder implements javax0.geci.api.GeneratorBuilder {
        public JavaDocSnippetInserter build() {
            return JavaDocSnippetInserter.this;
        }
    }
    //</editor-fold>
}
