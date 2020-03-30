package sandkev.differencer.clazz.loader;

import org.apache.commons.lang.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by kevin on 29/03/2020.
 */
public class Isolator {
    private static final Logger log = LoggerFactory.getLogger(Isolator.class);
    public static <T> T isolate(Class<T> resultInterface, Class<? extends T> classToIsolate,
                                Collection<String> namesOfAdditionalClassesToIsolate,
                                Object... constructorArgsOfClassToIsolate){
        List<String> namesOfClassesToIsolate = new ArrayList<>();
        namesOfClassesToIsolate.add(classToIsolate.getName());
        namesOfClassesToIsolate.addAll(namesOfAdditionalClassesToIsolate);
        ChildFirstClassLoader classLoader = new ChildFirstClassLoader(namesOfClassesToIsolate);
        return isolate(classLoader, resultInterface, new Class[]{resultInterface}, namesOfClassesToIsolate, constructorArgsOfClassToIsolate);
    }

    private static <T> T isolate(ChildFirstClassLoader classLoader, Class<? extends T> resultInterface, Class<?>[] allInterfacesImplementedByResult, Collection<String> namesOfClassesToIsolate,
                                 Object... constructorArgsOfClassToIsolate) {
        Object isolate = isolate(classLoader, namesOfClassesToIsolate, constructorArgsOfClassToIsolate);
        @SuppressWarnings("unchecked")
                T proxy = (T) IsolatedInvocationHandler.createIsolatedProxy(classLoader, isolate, allInterfacesImplementedByResult);
        return proxy;
    }

    private static Object isolate(ClassLoader classLoader, Collection<String> namesOfClassesToIsolate, Object... constructorArgs) {
        Object result = null;
        log.debug("classes to isolate: {}. Constructor argument(s): {}", namesOfClassesToIsolate, Arrays.asList(constructorArgs));
        String className = namesOfClassesToIsolate.iterator().next();
        try {
            Class<?> isolatedClass = Class.forName(className, true, classLoader);
            if(constructorArgs.length > 0){
                Constructor<?> constructor = ConstructorUtils.getMatchingAccessibleConstructor(isolatedClass, toClassArray(constructorArgs));
                result = constructor.newInstance(constructorArgs);
            }else {
                result = isolatedClass.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not isolate " + className);
        }
        return result;
    }

    private static Class[] toClassArray(Object... constructorArgs) {
        Class[] classes = new Class[constructorArgs.length];
        for(int n = 0; n < constructorArgs.length; n++){
            classes[n] = constructorArgs[n].getClass();
        }
        return classes;
    }
}
