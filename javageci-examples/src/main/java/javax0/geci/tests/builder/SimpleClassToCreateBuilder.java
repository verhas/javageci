package javax0.geci.tests.builder;

import javax0.geci.annotations.Geci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Geci("builder")
public class SimpleClassToCreateBuilder {

    private static class MyAggregated<T> {
        void add(int z){

        }
    }
    private final String willNotGetSetterBecauseFinal = "apple";
    @Geci("builder filter=true")
    private final List<String> willGetAggregatorOnly = new ArrayList<>();
    private Collection<Integer> willGetBoth;
    @Geci("builder filter=true checkNullInAggregator=false")
    private final MyAggregated<String> specialAggregator = new MyAggregated<>();
    private int z;
    @Geci("builder  setterPrefix='with'")
    private Double d;
    private SimpleClassToCreateBuilder selfWhyNot;

    public static class Habraka<T> {
        public void add(String z){}
        public void add(Number z){}
    }

    private Habraka<Integer> habraka;


    //<editor-fold id="builder" desc="builder code generated">
    @javax0.geci.annotations.Generated("builder")
    public static SimpleClassToCreateBuilder.Builder builder() {
        return new SimpleClassToCreateBuilder().new Builder();
    }

    public class Builder {
        @javax0.geci.annotations.Generated("builder")
        public Builder withD(Double d) {
            SimpleClassToCreateBuilder.this.d = d;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder habraka(SimpleClassToCreateBuilder.Habraka habraka) {
            SimpleClassToCreateBuilder.this.habraka = habraka;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addHabraka(Integer x) {
            if( SimpleClassToCreateBuilder.this.habraka == null ) {
                throw new IllegalArgumentException("Collection field habraka is null");
            }
            SimpleClassToCreateBuilder.this.habraka.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addHabraka(String x) {
            if( SimpleClassToCreateBuilder.this.habraka == null ) {
                throw new IllegalArgumentException("Collection field habraka is null");
            }
            SimpleClassToCreateBuilder.this.habraka.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder selfWhyNot(SimpleClassToCreateBuilder selfWhyNot) {
            SimpleClassToCreateBuilder.this.selfWhyNot = selfWhyNot;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addSpecialAggregator(int x) {
            SimpleClassToCreateBuilder.this.specialAggregator.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addWillGetAggregatorOnly(String x) {
            if( SimpleClassToCreateBuilder.this.willGetAggregatorOnly == null ) {
                throw new IllegalArgumentException("Collection field willGetAggregatorOnly is null");
            }
            SimpleClassToCreateBuilder.this.willGetAggregatorOnly.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder willGetBoth(java.util.Collection willGetBoth) {
            SimpleClassToCreateBuilder.this.willGetBoth = willGetBoth;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder addWillGetBoth(Integer x) {
            if( SimpleClassToCreateBuilder.this.willGetBoth == null ) {
                throw new IllegalArgumentException("Collection field willGetBoth is null");
            }
            SimpleClassToCreateBuilder.this.willGetBoth.add(x);
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public Builder z(int z) {
            SimpleClassToCreateBuilder.this.z = z;
            return this;
        }

        @javax0.geci.annotations.Generated("builder")
        public SimpleClassToCreateBuilder build() {
            return SimpleClassToCreateBuilder.this;
        }
    }
    //</editor-fold>
}
