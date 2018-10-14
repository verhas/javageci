package javax0.geci.fluent.collections;

public class ClassNameProvider {
    private int counter = 0;

    public String getNewClassName() {
        return "If" + counter++;
    }
}
