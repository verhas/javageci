package javax0.geci.tools.reflection;

import java.lang.reflect.Member;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
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

    static class Terminal extends SelectorNode {
        final Function<Member, Boolean> terminal;

        Terminal(Function<Member, Boolean> terminal) {
            this.terminal = terminal;
        }
    }

    static abstract class Regex extends SelectorNode {
        final Pattern regex;

        Regex(String regex) {
            this.regex = Pattern.compile(regex);
        }
    }

    static class Name extends Regex {
        Name(String regex) {
            super(regex);
        }
    }

    static class Signature extends Regex {
        Signature(String regex) {
            super(regex);
        }
    }

    static class Annotation extends Regex {
        Annotation(String regex) {
            super(regex);
        }
    }

    static class Returns extends Regex {
        Returns(String regex) {
            super(regex);
        }
    }


}
