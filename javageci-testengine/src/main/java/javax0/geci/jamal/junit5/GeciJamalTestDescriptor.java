package javax0.geci.jamal.junit5;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class GeciJamalTestDescriptor extends AbstractTestDescriptor {
    protected GeciJamalTestDescriptor(UniqueId uniqueId, String displayName, GeciTestSource source) {
        super(uniqueId, displayName, source);
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }
}
