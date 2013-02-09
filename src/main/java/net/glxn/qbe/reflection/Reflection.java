package net.glxn.qbe.reflection;

import net.glxn.qbe.reflection.exception.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.*;

import static java.lang.String.*;
import static java.util.Arrays.*;

/**
 * ReflectionUtil
 *
 * Class contains utility reflection methods.
 * See: {@link Reflection#hierarchy(Class)} and {@link Reflection#createInstance(Class)}
 */
public class Reflection {

    /**
     * This method returns a complete list of all {@link Class}es in the hierarchy of the object sent in as the argument including itself and all the way up to and including {@link Object}.<br/>
     * e.g. for class hierarchy:
     * <pre>
     *     Object
     *       |
     *    Vehicle
     *    |     |
     *   Car  Motorcycle
     * </pre>
     *
     * Calling {@code ClassHierarchyUtil.hierarchy(Car.class)} will return [Car.class, Vehicle.class, Object.class] as a list
     *
     * @param klazz the class for which to get the class hierarchy list
     * @return a list of classes that are in the hierarchy of the argument class from, and including, itself up to, and including, Object
     */
    public static List<Class<?>> hierarchy(Class<?> klazz) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        if (klazz != null) {
            list.add(klazz);
            list.addAll(hierarchy(klazz.getSuperclass()));
        }
        return list;
    }

    /**
     * This method creates a new instance of the given class. <br/>
     * Class must have a no args constructor. The constructor can be below public access(e.g. private) but it must be present.<br/>
     *
     * @param clazz class of the object you want instantiated. e.g. {@code User.class}
     * @param <T> the class type
     * @return an instance of the given class
     * @throws net.glxn.qbe.reflection.exception.ReflectionException if an instance could not be created
     */
    public static <T> T createInstance(Class<T> clazz) throws ReflectionException {
        String message = "Failed to create instance of type: %s Make sure the class has a no args constructor";

        T t;
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            if (!Modifier.isPublic(declaredConstructor.getModifiers())) {
                declaredConstructor.setAccessible(true);
            }
            t = declaredConstructor.newInstance();
        } catch (Exception e) {
            throw new ReflectionException(message, e);
        }
        if (t == null) {
            throw new ReflectionException(format(message, clazz.getCanonicalName()));
        }
        return t;
    }

    public static List<Field> fields(List<Class<?>> classes) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Class<?> clazz : classes) {
            fields.addAll(asList(clazz.getDeclaredFields()));
        }
        return fields;
    }

    public static Collection<Field> fieldsWithAnnotation(Class<? extends Annotation> annotation, List<Class<?>> classes) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Field field : fields(classes)) {
            Annotation fieldAnnotation = field.getAnnotation(annotation);
            if (fieldAnnotation != null) {
                fields.add(field);
            }
        }
        return fields;
    }
}

