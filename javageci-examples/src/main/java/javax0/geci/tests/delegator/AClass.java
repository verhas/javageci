package javax0.geci.tests.delegator;

public class AClass {

    public void abrakaDabra(String spell, int strengt) {
        System.out.println("abraka dabra");
    }

    public long anaesthesia(long time) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(time);
        return System.currentTimeMillis() - start;
    }

    protected void aaaargghhhh(){}

}
