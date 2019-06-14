package javax0.geci.tutorials.xmlbuilder;

import javax0.geci.annotations.Geci;

@Geci("fluent definedBy='javax0.geci.tests.fluent.TestFluent::xml'")
public class XmlBuilder {

    private static final int TABSIZE = 2;
    private StringBuilder xml = new StringBuilder();
    private boolean tagIsOpen = false;
    private String openedTag;
    private int tab = 0;

    public XmlBuilder copy() {
        var next = new XmlBuilder();
        next.xml = xml;
        next.tagIsOpen = tagIsOpen;
        next.openedTag = openedTag;
        next.tab = tab;
        return next;
    }

    private void closeTag() {
        if (tagIsOpen) {
            xml.append(">\n");
            tab += TABSIZE;
        }
        tagIsOpen = false;
    }

    private void openTag(String name) {
        if (tagIsOpen) {
            throw new IllegalArgumentException("Can not open a tag when one is still open: '" + openedTag + "'");
        }
        tabulate();
        xml.append("<").append(name);
        tagIsOpen = true;
        openedTag = name;
    }

    public void tag(String name) {
        closeTag();
        openTag(name);
    }

    public void attribute(String name, String value) {
        if (tagIsOpen) {
            xml.append(" ").append(name).append("=").append("\"").append(value).append("\"");
        } else {
            throw new IllegalArgumentException("Cannot add attribute when no tag is open.");
        }
    }

    private void tabulate() {
        xml.append("\n").append(tab > 0 ? String.format("%" + tab + "s", " ") : "");
    }

    public void text(String text) {
        closeTag();
        xml.append(text);
    }

    public void close() {
        closeTag();
        tab -= TABSIZE;
        if (tab < 0) tab = 0;
        tabulate();
        xml.append("</").append(openedTag).append(">");
    }

    @Override
    public String toString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + xml.toString();
    }

    //<editor-fold id="fluent">
    public static Edak start(){
        return new Wrapper();
    }
    public static class Wrapper implements Ukeg,Efeh,Ujaj,Ogoj,AutoCloseable,Edak,Acuh,Aduf,Ohug{
        private final javax0.geci.tutorials.xmlbuilder.XmlBuilder that;
        public Wrapper(javax0.geci.tutorials.xmlbuilder.XmlBuilder that){
            this.that = that;
        }
        public Wrapper(){
            this.that = new javax0.geci.tutorials.xmlbuilder.XmlBuilder();
        }
        public Wrapper tag(String arg1){
            var next = new Wrapper(that.copy());
            next.that.tag(arg1);
            return next;
        }
        public String toString(){
            return that.toString();
        }
        public Wrapper text(String arg1){
            var next = new Wrapper(that.copy());
            next.that.text(arg1);
            return next;
        }
        public Wrapper attribute(String arg1, String arg2){
            var next = new Wrapper(that.copy());
            next.that.attribute(arg1,arg2);
            return next;
        }
        public void close(){
            that.close();
        }
    }
    public interface Aduf {
        String toString();
    }
    public interface Ohug extends Ukeg,AutoCloseable {
        Ukeg text(String arg1);
    }
    public interface Efeh extends AutoCloseable,Ohug {
        Efeh attribute(String arg1, String arg2);
    }
    public interface Acuh {
        Efeh tag(String arg1);
    }
    public interface Ukeg extends AutoCloseable,Acuh,Aduf {}
    public interface Ujaj extends Ukeg,AutoCloseable {
        Ukeg text(String arg1);
    }
    public interface Ogoj extends Ujaj,AutoCloseable {
        Ogoj attribute(String arg1, String arg2);
    }
    public interface Edak {
        Ogoj tag(String arg1);
    }

    //</editor-fold>
}
