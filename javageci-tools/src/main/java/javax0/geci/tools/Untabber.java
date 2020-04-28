package javax0.geci.tools;

import java.util.ArrayList;
import java.util.List;

import static javax0.geci.tools.JVM8Tools.space;
import static javax0.geci.tools.JVM8Tools.stripLeading;

public class Untabber {

    public static List<String> untab(List<String> lines){
        return untab(lines,0);
    }
    public static List<String> untab(List<String> lines, int to){
        int untab = calculateTabbing(lines);
        final var untabbed = new ArrayList<String>();
        for (final var line : lines) {
            untabbed.add(space(to) + (line.length() >= untab ? line.substring(untab) : ""));
        }
        return untabbed;
    }

    private static int calculateTabbing(List<String> lines) {
        var min = Integer.MAX_VALUE;
        for (final var line : lines) {
            final var stripped = stripLeading(line).length();
            if (stripped > 0) {
                final var spaces = line.length() - stripped;
                if (spaces < min) {
                    min = spaces;
                }
            }
        }
        return min;
    }
}
