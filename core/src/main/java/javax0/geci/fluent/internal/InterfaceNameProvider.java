package javax0.geci.fluent.internal;

public class InterfaceNameProvider {
    private int counter = 0;
    private String lastInterface = null;

    public String getNewInterfaceName() {
        lastInterface = "If" + counter++;
        return lastInterface;
    }
    public String getLastInterfaceName() {
        return lastInterface;
    }
}
