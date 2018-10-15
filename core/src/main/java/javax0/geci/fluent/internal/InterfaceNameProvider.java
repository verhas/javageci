package javax0.geci.fluent.internal;

public class InterfaceNameProvider {
    private int counter = 0;

    public String getNewInterfaceName() {
        return "If" + counter++;
    }
}
