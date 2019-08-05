package javax0.geci.tools.reflection;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

class SelectorNode {

    static class Or extends SelectorNode {
        final Set<SelectorNode> subNodes = new HashSet<>();
    }

    static class And extends SelectorNode {
        final Set<SelectorNode> subNodes = new HashSet<>();
    }

    static class Not extends SelectorNode {
        final SelectorNode subNode;

        Not(SelectorNode subNode) {
            this.subNode = subNode;
        }
    }

    static class Converted extends SelectorNode {
        final SelectorNode subNode;
        final String converter;

        Converted(SelectorNode subNode, String converter) {
            this.converter = converter;
            this.subNode = subNode;
        }
    }


    static class Terminal extends SelectorNode {
        final String terminal;

        Terminal(String terminal) {
            this.terminal = terminal;
        }
    }

    static class Regex extends SelectorNode {
        final Pattern regex;
        final String name;

        Regex(String regex, String name) {
            this.regex = Pattern.compile(regex);
            this.name = name;
        }
    }
}
