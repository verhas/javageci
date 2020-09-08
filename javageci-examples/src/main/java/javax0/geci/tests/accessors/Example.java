package javax0.geci.tests.accessors;

import javax0.geci.annotations.Geci;
// snippet TestAccessor_result
@Geci("accessor")
public class Example {
    private int example;
    //<editor-fold id="accessor">
    public void setExample(int example) {
        this.example = example;
    }

    public int getExample() {
        return example;
    }

    //</editor-fold>
}
// end snippet