package javax0.geci.tools.reflection;

import javax0.geci.tools.MethodTool;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This class is deprecated. The functionality was moved to a separate library. Use
 *
 *             {@code com.javax0:refi:1.0.1} or later
 *
 * instead.
 *
 * Here we have this stup delegating all functionality to the implementation moved to a separate library to provide
 * compatibility in case there is any externally developed generator that uses this class.
 */
@SuppressWarnings("DanglingJavadoc")
@Deprecated
public class Selector<T> extends javax0.refi.selector.Selector<T> {

    private Selector(String expression) {
        super(expression);
    }
}
