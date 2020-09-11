package javax0.geci.tutorials.use;

import javax0.geci.annotations.Geci;

// snippet ExampleWithAnnotations
@Geci("accessor")
@Geci("builder")
public class ExampleWithAnnotations {
    private int example;
    //<editor-fold id="builder">
    @javax0.geci.annotations.Generated("builder")
    public static ExampleWithAnnotations.Builder builder() {
        return new ExampleWithAnnotations().new Builder();
    }

    @javax0.geci.annotations.Generated("builder")
    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder example(final int x) {
            ExampleWithAnnotations.this.example = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public ExampleWithAnnotations build() {
            return ExampleWithAnnotations.this;
        }
    }
    //</editor-fold>
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
