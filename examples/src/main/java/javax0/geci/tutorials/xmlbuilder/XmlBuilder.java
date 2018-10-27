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
        xml.append("\n").append(" ".repeat(tab));
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
    public static If7 start(){
        return new Wrapper();
    }
    public static class Wrapper implements If0,If2,If1,If4,If3,AutoCloseable,If6,If5,If7{
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
    public interface If0 {
        String toString();
    }
    public interface If2 extends If1,AutoCloseable {
        If1 text(String arg1);
    }
    public interface If3 extends If2,AutoCloseable {
        If3 attribute(String arg1, String arg2);
    }
    public interface If4 {
        If3 tag(String arg1);
    }
    public interface If1 extends If0,If4,AutoCloseable {}
    public interface If5 extends If1,AutoCloseable {
        If1 text(String arg1);
    }
    public interface If6 extends AutoCloseable,If5 {
        If6 attribute(String arg1, String arg2);
    }
    public interface If7 {
        If6 tag(String arg1);
    }
    //</editor-fold>
}
