package javax0.geci.tools.reflection;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public class ModifiersBuilder {
    final private StringBuilder s = new StringBuilder();
    final private int modifiers;
    private boolean isMethod = true;

    public ModifiersBuilder(int modifiers) {
        this.modifiers = modifiers;
    }

    private void check(Predicate<Integer> predicate, String text) {
        if (predicate.test(modifiers)) {
            s.append(text);
        }
    }

    public ModifiersBuilder field() {
        isMethod = false;
        return this;
    }

    /**
     * @return the modifiers as string. Access modifiers are th first (guaranteed) and they start from private towards
     * public.
     */
    @Override
    public String toString() {
        s.setLength(0);
        check(Modifier::isPrivate, "private ");
        check(Modifier::isProtected, "protected ");
        check(Modifier::isPublic, "public ");
        check(Modifier::isFinal, "final ");
        check(Modifier::isStatic, "static ");
        check(Modifier::isSynchronized, "synchronized ");
        check(Modifier::isVolatile, "volatile ");
        check(Modifier::isStrict, "strictfp ");
        if (!isMethod) {
            check(Modifier::isTransient, "transient ");
        }

        return s.toString();
    }
}
