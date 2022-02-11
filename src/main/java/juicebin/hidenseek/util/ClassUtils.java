package juicebin.hidenseek.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ClassUtils {

    public static Map<Method, Annotation> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final Map<Method, Annotation> map = new HashMap<>();
        Class<?> klass = type;
        while (klass != Object.class) {
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    Annotation annotInstance = method.getAnnotation(annotation);
                    map.put(method, annotInstance);
                }
            }
            klass = klass.getSuperclass();
        }
        return map;
    }

}
