package javax0.geci.tutorials.use;

//snippet ExampleWithEditorFolds
public class ExampleWithEditorFolds {
    private int example;
    //<editor-fold id="accessor">
    public void setExample(int example) {
        this.example = example;
    }

    public int getExample() {
        return example;
    }

    //</editor-fold>
    //<editor-fold id="builder">
    @javax0.geci.annotations.Generated("builder")
    public static ExampleWithEditorFolds.Builder builder() {
        return new ExampleWithEditorFolds().new Builder();
    }

    @javax0.geci.annotations.Generated("builder")
    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder example(final int x) {
            ExampleWithEditorFolds.this.example = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public ExampleWithEditorFolds build() {
            return ExampleWithEditorFolds.this;
        }
    }
    //</editor-fold>
}
// end snippet