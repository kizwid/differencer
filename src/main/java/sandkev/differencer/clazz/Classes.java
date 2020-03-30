package sandkev.differencer.clazz;

import java.lang.reflect.Modifier;

/**
 * Created by kevin on 29/03/2020.
 */
public class Classes {

    public static final Class<?> forName(String className){
        try {
            return Class.forName(className);
        }catch (ClassNotFoundException e){
            throw new IllegalArgumentException("className: '" + className + "'", e);
        }
    }

    public static final <T> T newInstance(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new IllegalArgumentException("class: " + clazz);
        }
    }

    public static final Object newInstance(String className){
        Class<?> clazz = forName(className);
        if( clazz.isInterface()){
            throw new IllegalArgumentException("class is interface: " + clazz);
        }
        if( clazz.isPrimitive()){
            throw new IllegalArgumentException("class is primitive: " + clazz);
        }
        if(Modifier.isAbstract(clazz.getModifiers())){
            throw new IllegalArgumentException("class is abstract: " + clazz);
        }
        return newInstance(clazz);
    }

}

