package javax0.geci.tests.builder;

import javax0.geci.annotations.Geci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Geci("builder factory='factory()'")
public class SimpleClassToCreateBuilder {

    private static class MyAggregated<T> {
        void add(int z){

        }
    }
    private final String willNotGetSetterBecauseFinal = "apple";
    @Geci("builder filter=true")
    private final List<String> willGetAggregatorOnly = new ArrayList<>();
    @Geci("builder filter=true aggregatorMethod=''")
    private final List<String> willNotGetEvenAggregator = new ArrayList<>();
    private Collection<Integer> willGetBoth;
    @Geci("builder filter=true checkNullInAggregator=false argumentVariable='birkaBruhahaJavaGeciMuhaha'")
    private final MyAggregated<String> specialAggregator = new MyAggregated<>();
    private int z;
    @Geci("builder  setterPrefix='with'")
    private Double d;
    private SimpleClassToCreateBuilder selfWhyNot;

    public static SimpleClassToCreateBuilder factory(){
        return new SimpleClassToCreateBuilder();
    }

    public static class Habraka<T> {
        public void add(String z){}
        public void add(Number z){}
    }

    private Habraka<Integer> habraka;


    //<editor-fold id="builder" desc="builder code generated">
    @javax0.geci.annotations.Generated("builder")
    public static SimpleClassToCreateBuilder.Builder builder() {
        return factory().new Builder();
    }

    @javax0.geci.annotations.Generated("builder")
    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder withD(final Double x) {
            SimpleClassToCreateBuilder.this.d = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder habraka(final SimpleClassToCreateBuilder.Habraka x) {
            SimpleClassToCreateBuilder.this.habraka = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addHabraka(final Integer x) {
            if( SimpleClassToCreateBuilder.this.habraka == null ) {
                throw new IllegalArgumentException("Collection field habraka is null");
            }
            SimpleClassToCreateBuilder.this.habraka.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addHabraka(final String x) {
            if( SimpleClassToCreateBuilder.this.habraka == null ) {
                throw new IllegalArgumentException("Collection field habraka is null");
            }
            SimpleClassToCreateBuilder.this.habraka.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder selfWhyNot(final SimpleClassToCreateBuilder x) {
            SimpleClassToCreateBuilder.this.selfWhyNot = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addSpecialAggregator(final int birkaBruhahaJavaGeciMuhaha) {
            SimpleClassToCreateBuilder.this.specialAggregator.add(birkaBruhahaJavaGeciMuhaha);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addWillGetAggregatorOnly(final String x) {
            if( SimpleClassToCreateBuilder.this.willGetAggregatorOnly == null ) {
                throw new IllegalArgumentException("Collection field willGetAggregatorOnly is null");
            }
            SimpleClassToCreateBuilder.this.willGetAggregatorOnly.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder willGetBoth(final java.util.Collection x) {
            SimpleClassToCreateBuilder.this.willGetBoth = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addWillGetBoth(final Integer x) {
            if( SimpleClassToCreateBuilder.this.willGetBoth == null ) {
                throw new IllegalArgumentException("Collection field willGetBoth is null");
            }
            SimpleClassToCreateBuilder.this.willGetBoth.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder z(final int x) {
            SimpleClassToCreateBuilder.this.z = x;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public SimpleClassToCreateBuilder build() {
            return SimpleClassToCreateBuilder.this;
        }
    }
    //</editor-fold>
}
