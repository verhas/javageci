package javax0.geci.jamal_test.unittestproxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax0.geci.jamal_test.sample.TestGenerateArticle;

public class UnitTestWithGeneratedUnitTestProxy {

    /*!jamal
    {%@import res:unittestproxy.jim%}\

    {%beginCode SystemUnderTest proxy generated%}
    {%proxy javax0.geci.jamal_test.unittestproxy.TestSystemUnderTest%}
    {%endCode%}
     */

    //<editor-fold desc="SystemUnderTest proxy generated">

    private static class TestSystemUnderTest {
        private final javax0.geci.jamal_test.unittestproxy.TestSystemUnderTest sut = new javax0.geci.jamal_test.unittestproxy.TestSystemUnderTest();

        private void privateChildVoidMethodArgless() throws Exception {
            Method m = sut.getClass().getDeclaredMethod("privateChildVoidMethodArgless");
            m.setAccessible(true);
            m.invoke(sut);
            }

        private void privateChildVoidMethodOneArg(int arg0) throws Exception {
            Method m = sut.getClass().getDeclaredMethod("privateChildVoidMethodOneArg",int.class);
            m.setAccessible(true);
            m.invoke(sut,arg0);
            }

        private void privateChildVoidMethodOneArgObject(Object arg0) throws Exception {
            Method m = sut.getClass().getDeclaredMethod("privateChildVoidMethodOneArgObject",Object.class);
            m.setAccessible(true);
            m.invoke(sut,arg0);
            }

        private void privateChildVoidMethodOneArgTestClass(javax0.geci.jamal_test.sample.SystemUnderTest arg0) throws Exception {
            Method m = sut.getClass().getDeclaredMethod("privateChildVoidMethodOneArgTestClass",javax0.geci.jamal_test.sample.SystemUnderTest.class);
            m.setAccessible(true);
            m.invoke(sut,arg0);
            }

        private void privateChildVoidMethodVararg(Object arg0,String... arg1) throws Exception {
            Method m = sut.getClass().getDeclaredMethod("privateChildVoidMethodVararg",Object.class,String[].class);
            m.setAccessible(true);
            m.invoke(sut,arg0,arg1);
            }


        private void publicChildVoidMethodArgless()  {
            sut.publicChildVoidMethodArgless();
            }

        private void publicChildVoidMethodOneArg(int arg0)  {
            sut.publicChildVoidMethodOneArg(arg0);
            }

        private void publicChildVoidMethodOneArgObject(Object arg0)  {
            sut.publicChildVoidMethodOneArgObject(arg0);
            }

        private void publicChildVoidMethodOneArgTestClass(javax0.geci.jamal_test.sample.SystemUnderTest arg0)  {
            sut.publicChildVoidMethodOneArgTestClass(arg0);
            }

        private void publicParentVoidMethodArgless()  {
            sut.publicParentVoidMethodArgless();
            }

        private void publicParentVoidMethodOneArg(int arg0)  {
            sut.publicParentVoidMethodOneArg(arg0);
            }

        private void publicParentVoidMethodOneArgObject(Object arg0)  {
            sut.publicParentVoidMethodOneArgObject(arg0);
            }

        private void publicParentVoidMethodOneArgTestClass(javax0.geci.jamal_test.sample.SystemUnderTest arg0)  {
            sut.publicParentVoidMethodOneArgTestClass(arg0);
            }

        private void packageChildVoidMethodArgless()  {
            sut.packageChildVoidMethodArgless();
            }

        private void packageChildVoidMethodOneArg(int arg0)  {
            sut.packageChildVoidMethodOneArg(arg0);
            }

        private void packageChildVoidMethodOneArgObject(Object arg0)  {
            sut.packageChildVoidMethodOneArgObject(arg0);
            }

        private void packageChildVoidMethodOneArgTestClass(javax0.geci.jamal_test.sample.SystemUnderTest arg0)  {
            sut.packageChildVoidMethodOneArgTestClass(arg0);
            }

        private void packageParentVoidMethodArgless()  {
            sut.packageParentVoidMethodArgless();
            }

        private void packageParentVoidMethodOneArg(int arg0)  {
            sut.packageParentVoidMethodOneArg(arg0);
            }

        private void packageParentVoidMethodOneArgObject(Object arg0)  {
            sut.packageParentVoidMethodOneArgObject(arg0);
            }

        private void packageParentVoidMethodOneArgTestClass(javax0.geci.jamal_test.sample.SystemUnderTest arg0)  {
            sut.packageParentVoidMethodOneArgTestClass(arg0);
            }


        private void setPrivateChildIntField(int privateChildIntField) throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildIntField");
            f.setAccessible(true);
            f.set(sut,privateChildIntField);
            }

        private void setPrivateChildObjectField(Object privateChildObjectField) throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildObjectField");
            f.setAccessible(true);
            f.set(sut,privateChildObjectField);
            }

        private void setPrivateChildTestField(javax0.geci.jamal_test.sample.SystemUnderTest privateChildTestField) throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildTestField");
            f.setAccessible(true);
            f.set(sut,privateChildTestField);
            }

        private void setStaticField(Object staticField) throws Exception {
            Field f = sut.getClass().getDeclaredField("staticField");
            f.setAccessible(true);
            f.set(sut,staticField);
            }


        private int getPrivateChildIntField() throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildIntField");
            f.setAccessible(true);
            return (int)f.get(sut);
            }

        private Object getPrivateChildObjectField() throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildObjectField");
            f.setAccessible(true);
            return (Object)f.get(sut);
            }

        private javax0.geci.jamal_test.sample.SystemUnderTest getPrivateChildTestField() throws Exception {
            Field f = sut.getClass().getDeclaredField("privateChildTestField");
            f.setAccessible(true);
            return (javax0.geci.jamal_test.sample.SystemUnderTest)f.get(sut);
            }

        private Object getPrivateFinalChildObjectField() throws Exception {
            Field f = sut.getClass().getDeclaredField("privateFinalChildObjectField");
            f.setAccessible(true);
            return (Object)f.get(sut);
            }

        private Object getStaticField() throws Exception {
            Field f = sut.getClass().getDeclaredField("staticField");
            f.setAccessible(true);
            return (Object)f.get(sut);
            }


        private void setPackageChildIntField(int packageChildIntField) {
            sut.packageChildIntField = packageChildIntField;
            }

        private void setPackageChildObjectField(Object packageChildObjectField) {
            sut.packageChildObjectField = packageChildObjectField;
            }

        private void setPackageChildTestField(javax0.geci.jamal_test.sample.SystemUnderTest packageChildTestField) {
            sut.packageChildTestField = packageChildTestField;
            }

        private void setPackageParentIntField(int packageParentIntField) {
            sut.packageParentIntField = packageParentIntField;
            }

        private void setPackageParentObjectField(Object packageParentObjectField) {
            sut.packageParentObjectField = packageParentObjectField;
            }

        private void setPackageParentTestField(javax0.geci.jamal_test.sample.SystemUnderTest packageParentTestField) {
            sut.packageParentTestField = packageParentTestField;
            }

        private void setPublicChildIntField(int publicChildIntField) {
            sut.publicChildIntField = publicChildIntField;
            }

        private void setPublicChildObjectField(Object publicChildObjectField) {
            sut.publicChildObjectField = publicChildObjectField;
            }

        private void setPublicChildTestField(javax0.geci.jamal_test.sample.SystemUnderTest publicChildTestField) {
            sut.publicChildTestField = publicChildTestField;
            }

        private void setPublicParentIntField(int publicParentIntField) {
            sut.publicParentIntField = publicParentIntField;
            }

        private void setPublicParentObjectField(Object publicParentObjectField) {
            sut.publicParentObjectField = publicParentObjectField;
            }

        private void setPublicParentTestField(javax0.geci.jamal_test.sample.SystemUnderTest publicParentTestField) {
            sut.publicParentTestField = publicParentTestField;
            }

        private void setWuff(TestGenerateArticle wuff) {
            sut.wuff = wuff;
            }


        private int getPackageChildIntField() {
            return sut.packageChildIntField;
            }

        private Object getPackageChildObjectField() {
            return sut.packageChildObjectField;
            }

        private javax0.geci.jamal_test.sample.SystemUnderTest getPackageChildTestField() {
            return sut.packageChildTestField;
            }

        private int getPackageParentIntField() {
            return sut.packageParentIntField;
            }

        private Object getPackageParentObjectField() {
            return sut.packageParentObjectField;
            }

        private javax0.geci.jamal_test.sample.SystemUnderTest getPackageParentTestField() {
            return sut.packageParentTestField;
            }

        private int getPublicChildIntField() {
            return sut.publicChildIntField;
            }

        private Object getPublicChildObjectField() {
            return sut.publicChildObjectField;
            }

        private javax0.geci.jamal_test.sample.SystemUnderTest getPublicChildTestField() {
            return sut.publicChildTestField;
            }

        private Object getPublicFinalChildObjectField() {
            return sut.publicFinalChildObjectField;
            }

        private int getPublicParentIntField() {
            return sut.publicParentIntField;
            }

        private Object getPublicParentObjectField() {
            return sut.publicParentObjectField;
            }

        private javax0.geci.jamal_test.sample.SystemUnderTest getPublicParentTestField() {
            return sut.publicParentTestField;
            }

        private TestGenerateArticle getWuff() {
            return sut.wuff;
            }

    }

    //</editor-fold>
    //__END__
}
