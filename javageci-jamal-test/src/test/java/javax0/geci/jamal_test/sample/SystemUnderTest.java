package javax0.geci.jamal_test.sample;

/**
 * A simple demonstration class that can be used as a system under test to demonstrate how to invoke private methods and
 * access private fields from a unit test.
 */
// snippet SystemUnderTest
public class SystemUnderTest {

    /**
     * A private counter that is initialized only once in production but in test we need to set it many times.
     */
    private int counter = 0;

    /**
     * Count the internal counter z times
     *
     * @param z the number of times we count
     * @return the new value of the counter
     */
    public int count(int z) {
        while (z > 0) {
            z--;
            increment();
        }
        return counter;
    }

    /**
     * Increment the counter by one.
     */
    private void increment(){
            counter++;
    }

}
// end snippet
