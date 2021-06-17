package javax0.geci.jamal_test.unittestproxy;

import javax0.geci.jamal_test.sample.SystemUnderTest;

public abstract class ParentSystemUnderTest {

    private int privateParentIntField;
    private SystemUnderTest privateParentTestField;
    private Object privateParentObjectField;
    int packageParentIntField;
    SystemUnderTest packageParentTestField;
    Object packageParentObjectField;
    public int publicParentIntField;
    public SystemUnderTest publicParentTestField;
    public Object publicParentObjectField;

    private void privateParentVoidMethodArgless(){}
    private void privateParentVoidMethodOneArg(int k){}
    private void privateParentVoidMethodOneArgTestClass(SystemUnderTest k){}
    private void privateParentVoidMethodOneArgObject(Object k){}

    void packageParentVoidMethodArgless(){}
    void packageParentVoidMethodOneArg(int k){}
    void packageParentVoidMethodOneArgTestClass(SystemUnderTest k){}
    void packageParentVoidMethodOneArgObject(Object k){}

    public void publicParentVoidMethodArgless(){}
    public void publicParentVoidMethodOneArg(int k){}
    public void publicParentVoidMethodOneArgTestClass(SystemUnderTest k){}
    public void publicParentVoidMethodOneArgObject(Object k){}
}
