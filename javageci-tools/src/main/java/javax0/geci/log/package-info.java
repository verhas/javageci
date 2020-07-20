/**
 * <p>This package contains a simple logger facade that is used to retain Java 8 compatibility.</p>
 *
 * <p>Java::Geci is developed using Java 11 features but strictly using only Java 8 JDK libraries. That way when the
 * code is compiled using the JBEL annotation processor the Java compiler will generate Java 8 compatible byte code from
 * the Java 11 source code.</p>
 *
 * <p>The only exception where we did not want to lose the Java 9+ feature is the Java 9+ logging. To have a
 * flexible logging that can use Java 9+ logging possibilities when available we use reflection when the {@link
 * javax0.geci.log.Logger} class is loaded.</p>
 */
package javax0.geci.log;