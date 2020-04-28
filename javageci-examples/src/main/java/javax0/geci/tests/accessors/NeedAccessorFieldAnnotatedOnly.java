package javax0.geci.tests.accessors;

public class NeedAccessorFieldAnnotatedOnly {

    @Getter
    private final String apple = "";
    private int birnen;

    int packge;

    protected boolean truth;
    protected int not_this;
    //<editor-fold id="SettersGetters">
    public String getApple() {
        return apple;
    }

    //</editor-fold>
}
