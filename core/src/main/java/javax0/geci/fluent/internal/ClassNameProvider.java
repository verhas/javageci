package javax0.geci.fluent.internal;

public class ClassNameProvider {
    private int counter = 0;

    public String getNewClassName() {
        return "If" + counter++;
    }
}
