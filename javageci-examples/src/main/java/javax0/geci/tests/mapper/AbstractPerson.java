package javax0.geci.tests.mapper;

public class AbstractPerson {
    public boolean willMapPublicInherited;
    protected int willMapProtectedInherited;
    float willMapDefaultInherited;
    private String willNotMap;

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof AbstractPerson)) return false;

        AbstractPerson other = (AbstractPerson) o;
        if(willMapPublicInherited != other.willMapPublicInherited) return false;
        if(willMapProtectedInherited != other.willMapProtectedInherited) return false;
        return (willMapDefaultInherited == other.willMapDefaultInherited);
    }
}
