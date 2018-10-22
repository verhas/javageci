package javax0.geci.fluent.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InterfaceList {
    private final Set<String> interfaceSet = new HashSet<>();
    private boolean lastWhen;

    private InterfaceList(boolean needsWrapperInterface) {
        if (needsWrapperInterface) {
            interfaceSet.add("WrapperInterface");
        }
    }

    public static InterfaceList builderFor(MethodCollection methods) {
        return new InterfaceList(methods.needWrapperInterface());
    }

    public InterfaceList set(Set<String> interfaces) {
        this.interfaceSet.addAll(interfaces);
        return this;
    }

    public InterfaceList set(String... interfaces) {
        this.interfaceSet.addAll(Arrays.stream(interfaces).collect(Collectors.toSet()));
        return this;
    }

    public InterfaceList when(boolean flag) {
        lastWhen = flag;
        return this;
    }

    public InterfaceList then(String... interfaces) {
        if (lastWhen) {
            set(interfaces);
        }
        return this;
    }


    public String buildList() {
        if (interfaceSet.isEmpty()) {
            return "";
        } else {
            return " extends " + String.join(",", interfaceSet);
        }
    }
}
