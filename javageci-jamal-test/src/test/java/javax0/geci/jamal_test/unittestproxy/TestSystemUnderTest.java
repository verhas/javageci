package javax0.geci.jamal_test.unittestproxy;

import javax0.geci.jamal_test.sample.SystemUnderTest;
import javax0.geci.jamal_test.sample.TestGenerateArticle;

public class TestSystemUnderTest extends ParentSystemUnderTest{

    private static Object staticField;
    private int privateChildIntField;
    private SystemUnderTest privateChildTestField;
    private Object privateChildObjectField;
    private final Object privateFinalChildObjectField = null;
    int packageChildIntField;
    SystemUnderTest packageChildTestField;
    Object packageChildObjectField;
    public int publicChildIntField;
    TestGenerateArticle wuff;
    public SystemUnderTest publicChildTestField;
    public Object publicChildObjectField;
    public final Object publicFinalChildObjectField = null;

    private void privateChildVoidMethodArgless(){}
    private void privateChildVoidMethodOneArg(int k){}
    private void privateChildVoidMethodOneArgTestClass(SystemUnderTest k){}
    private void privateChildVoidMethodOneArgObject(Object k){}
    private void privateChildVoidMethodVararg(Object k, String ...messages){}

    void packageChildVoidMethodArgless(){}
    void packageChildVoidMethodOneArg(int k){}
    void packageChildVoidMethodOneArgTestClass(SystemUnderTest k){}
    void packageChildVoidMethodOneArgObject(Object k){}

    public void publicChildVoidMethodArgless(){}
    public void publicChildVoidMethodOneArg(int k){}
    public void publicChildVoidMethodOneArgTestClass(SystemUnderTest k){}
    public void publicChildVoidMethodOneArgObject(Object k){}

}
