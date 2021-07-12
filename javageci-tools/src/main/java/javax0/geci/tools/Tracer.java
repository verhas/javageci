package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * <p>Tracing the execution of code generation.</p>
 *
 * <p>This utility class provides methods to collect hierarchical trace messages about the execution of the code
 * generation. These messages can be dumped into an XML formatted file after the code generation has finished which is a
 * neat, not too sexy these days but highly supported format by different code editors to navigate along the
 * hierarchical structure.</p>
 *
 * <p>The hierarchical structure is composed of nodes. A node has a message, a tag, a cData, a parent and children
 * nodes. The children node array is empty for leaf nodes. The parent node is {@code null} in the root node. cData nodes
 * are always leaf nodes.</p>
 *
 * <p>When a node is converted to XML the tag will be used as the XML tag, the message will be the value of a {@code
 * msg=""} attribute. If there is a cData then this will be enclosed between the opening and closing XML tags. Children
 * nodes if any, will be XML children nodes.</p>
 *
 * <p>Note that the public methods of the class are {@code static}. This has the advantage that the callers do not
 * need access to any instance. The drawback is that this approach is not thread safe at all. However, this should
 * be an acceptable limitation because the functionality is to be used only when debugging some configuration that
 * runs during unit test execution. Even if the unit tests are executed parallel, multiple tests the same time, only
 * one of them should use tracing. Why would anyone want to debug multiple unit tests running parallel?</p>
 *
 * <p>The typical use is to call {@code Tracer.log("message")} tolog messages and when there is a new structure level to
 * open then call {@code Tracer.push("message")} at the start of the block and {@code Tracer.pop()} at the end. Since
 * the class implements the {@code Autoclosable} interface you can also write</p>
 *
 * <pre>{@code
 *   try( Tracer pos = Tracer.push("message") ){
 *       ....
 *   }
 * }</pre>
 *
 * The advantage of this use is that the structure is restored to the right level if there is a bug in the underlying
 * code that calls too many {@code pop()} methods.
 *
 * <p> By default, or after calling the methof {@link #off()} the tracing is switched off. It means that all methods
 * just return and do nothing. Thus if the tracing is to be used the application first has to call {@link #on()}.
 * Note that when using Java::Geci generators the {@link javax0.geci.api.Geci#trace(String)} call will switch
 * the tracing on and at the end of the code generator the tracing will be switched off again.</p>
 *
 */
public class Tracer implements AutoCloseable {
    private static class TracerPop extends RuntimeException {
    }
    private static final Logger log = LoggerFactory.getLogger();
    private static final String DEFAULT_TAG = "log";
    private static Tracer root;
    private static Tracer current;
    private static Tracer last;
    private String popTrace;
    private static final Tracer FAKE = new Tracer(null, null, null, null);

    private final Tracer parent;
    private final ArrayList<Tracer> children = new ArrayList<>();
    private String message;
    private final String tag;
    private final String cData;

    private Tracer(Tracer parent, String message, String tag, String cData) {
        this.parent = parent;
        this.message = message;
        this.tag = tag;
        this.cData = cData;
    }

    /**
     * Reset the tracer subsystem and switch it on.
     */
    public static void on() {
        root = new Tracer(null, "tracer root", "trace", null);
        resetCurrentAndLast();
    }

    /**
     * Reset the tracer subsystem and switch it off.
     */
    public static void off() {
        root = null;
        resetCurrentAndLast();
    }

    private static void resetCurrentAndLast() {
        current = root;
        last = root;
    }

    /**
     * Walks in the structure upward until it finds a level that has the given tag. If the tag is not found then a
     * GeciException will be appended with the full trace text lines as cData after the current node.
     *
     * @param tag the tag that we are looking for
     * @return the node found
     */
    private static Tracer walkUpTo(final String tag) {
        var walk = last;
        while (walk != null && !walk.tag.equals(tag)) {
            walk = walk.parent;
        }
        if( walk == null ){
            final var e = new GeciException("Walking upward in trace there is no tag '"+tag+"'");
            log(e);
        }
        return walk;
    }

    /**
     * Prepend the message {@code msg} to the already existing message of the last node that had the {@code tag} in the
     * hierarchy upward.
     *
     * @param tag the name of the tag to which want to prepend the text
     * @param msg the message we want to insert before the already existing message
     */
    public static void prepend(final String tag, final String msg) {
        if (last == null) return;
        final var my = walkUpTo(tag);
        if (my != null) {
            if( my.message == null ){
                my.message = "";
            }
            my.message = msg + my.message;
        }
    }

    /**
     * The same as {@link #prepend(String, String)} but the message is appended after the currently existing message.
     *
     * @param tag the name of the tag to which want to append the text
     * @param msg the message we want to insert before the already existing message
     */
    public static void append(final String tag, final String msg) {
        if (last == null) return;
        final var my = walkUpTo(tag);
        if (my != null) {
            if( my.message == null ){
                my.message = "";
            }
            my.message = my.message + msg;
        }
    }

    /**
     * Prepend the text before the text of the message of the node that was inserted last.
     *
     * @param msg the message to insert before the existing one
     */
    public static void prepend(final String msg) {
        if (last == null) return;
        if( last.message == null ){
            last.message = "";
        }
        last.message = msg + last.message;
    }

    /**
     * Append the text after the text of the message of the node that was inserted last.
     *
     * @param msg the message to append after the existing one
     */
    public static void append(final String msg) {
        if (last == null) return;
        if( last.message == null ){
            last.message = "";
        }
        last.message = last.message + msg;
    }

    /**
     * Log a message. The tag will be {@code log}
     *
     * @param msg the message to log
     */
    public static void log(final String msg) {
        log(DEFAULT_TAG, msg);
    }

    /**
     * Log a message with the given tag.
     *
     * @param tag the tag of the node
     * @param msg the message of the node
     */
    public static void log(final String tag, final String msg) {
        log(tag, msg, null);
    }

    /**
     * Log a message with the given tag and the cData.
     *
     * @param tag   the tag of the log item
     * @param msg   the message
     * @param cData the cData of the node
     */
    public static void log(final String tag, final String msg, String cData) {
        if (root == null) return;
        current.children.add(last = new Tracer(current, msg, tag, cData));
    }

    /**
     * Open a new level of nodes.
     *
     * @param msg the message of the node under which the new log nodes will get
     * @return the current node that can be passed to {@link #pop(Tracer)}.
     */
    public static Tracer push(String msg) {
        return push(DEFAULT_TAG, msg);
    }

    /**
     * Open a new level of nodes.
     *
     * @param tag is the tag of the node under which the new log nodes will get
     * @param msg the message of the node under which the new log nodes will get
     * @return the current node that can be passed to {@link #pop(Tracer)}.
     */
    public static Tracer push(String tag, String msg) {
        if (root == null) return FAKE;
        final var actual = current;
        last = new Tracer(current, msg, tag, null);
        current.children.add(last);
        current = last;
        return actual;
    }

    /**
     * Return one level higher. If it is not possible to return one level higher then the method will create a new
     * GeciException and insert it as a cData node
     */
    public static void pop() {
        if (root == null) return;
        if (current.parent != null) {
            current.popTrace = new TracerPop().getStackTrace()[1].toString();
            current = current.parent;
        } else {
            final var e = new GeciException("Too many Tracer.pop() calls");
            log(e);
        }
    }

    /**
     * Return to the level specified as argument. If there were too many pops and the current pointer is above where
     * it should be then it will put exception cdata messages for all the pops that has happened from the deepest level
     * to this point.
     *
     * @param actual the node that was returned by {@link #push(String)} or {@link #push(String, String)}
     */
    public static void pop(Tracer actual) {
        if (root == null) return;
        Tracer stepper;
        for (stepper = current.parent; stepper != null && stepper != actual; stepper = stepper.parent) ;
        if (stepper == null) {
            stepper = actual;
            Tracer child;
            while ((child = lastChild(stepper)) != null && child.popTrace != null) {
                stepper = child;
            }
            current = actual;
            try (final var tracer = push("PopTrace", null)) {
                while (stepper != null && stepper.popTrace != null) {
                    log("Pop",stepper.popTrace);
                    stepper = stepper.parent;
                }
            }
        }
        current = actual;
    }

    private static Tracer lastChild(Tracer tracer) {
        if (tracer.children.isEmpty()) {
            return null;
        }
        return tracer.children.get(tracer.children.size() - 1);
    }

    /**
     * This is the only non static method that is supposed to be called by the {@code try-with-resources} command.
     */
    @Override
    public void close() {
        pop(this);
    }

    /**
     * Log an exception. The tag will be {@code ERROR} and the exception full stack trace will be added to the trace as
     * cData.
     *
     * @param e the exceptin to append to the trace
     */
    public static void log(Throwable e){
        log("ERROR", null, exceptionToString(e));
    }

    private static String exceptionToString(Throwable e) {
        String result = "";
        try (final var sw = new StringWriter();
             final var pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            result = sw.toString();
        } catch (IOException ignored) {
        }
        return result;
    }

    /**
     * Convert the current trace to xml formatted text and write it into the file.
     *
     * @param fileName the name of the file here to write the trace
     * @throws IOException in case the trace file cannot be written
     */
    public static void dumpXML(String fileName) throws IOException {
        if (root == null) return;
        final var sb = new StringBuilder();
        dumpXML(sb);
        final var file = new File(fileName);
        try (final var fos = new FileOutputStream(file)) {
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Convert the current trace to xml formatted text and write it into a StringBuilder.
     *
     * @param sb the output where the string will be put
     */
    public static void dumpXML(StringBuilder sb) {
        if (root == null) return;
        dumpXML(root, sb, 0);
    }

    private static void dumpXML(Tracer node, StringBuilder sb, int tab) {
        if( tab > 200 ){
            sb.append(" ".repeat(tab)).append("<FATAL message=\"Nesting of trace messages is too deep, probably internal error.\"/>");
            return;
        }
        final String messageTag = node.message != null ? " msg=\"" + escape(node.message) + "\"" : "";
        if (node.children.isEmpty()) {
            if (node.cData == null) {
                sb.append(" ".repeat(tab)).append("<").append(node.tag).append(messageTag).append("/>").append("\n");
            } else {
                sb.append(" ".repeat(tab)).append("<").append(node.tag).append(messageTag).append(">").append("\n");
                sb.append("<![CDATA[").append(node.cData).append("]]>\n");
                sb.append(" ".repeat(tab)).append("</").append(node.tag).append(">\n");
            }
        } else {
            sb.append(" ".repeat(tab)).append("<").append(node.tag).append(messageTag).append(">").append("\n");
            node.children.forEach(c -> dumpXML(c, sb, tab + 2));
            sb.append(" ".repeat(tab)).append("</").append(node.tag).append(">\n");
        }
    }

    private static String escape(String s) {
        return s.replace("&","&amp;")
            .replace("<","&lt;")
            .replace(">","&gt;")
            .replace("\"", "&quot;");
    }



}
