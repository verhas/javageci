package javax0.geci.tests.accessors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NeedAccessorFieldAnnotatedOnly {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Getter {
    }

    @Getter
    private final String apple = "";
    private int birnen;

    int packge;

    protected boolean truth;
    protected int not_this;


    //<editor-fold id="accessor">
    public String getApple(){
        return apple;
    }

    //</editor-fold>
}
