package sandkev.differencer.clazz;

import java.util.*;

/**
 * Created by kevin on 29/03/2020.
 */
public class ClassResolver implements IClassResolver {

    public static final Map<String, Class<?>> nameToPrimitiveMap = new HashMap<>();
    static {
        nameToPrimitiveMap.put(boolean.class.getName(), boolean.class);
        nameToPrimitiveMap.put(byte.class.getName(), byte.class);
        nameToPrimitiveMap.put(short.class.getName(), short.class);
        nameToPrimitiveMap.put(int.class.getName(), int.class);
        nameToPrimitiveMap.put(long.class.getName(), long.class);
        nameToPrimitiveMap.put(float.class.getName(), float.class);
        nameToPrimitiveMap.put(double.class.getName(), double.class);
        nameToPrimitiveMap.put(char.class.getName(), char.class);
        nameToPrimitiveMap.put("String", String.class);
        nameToPrimitiveMap.put("Class", Class.class);
        nameToPrimitiveMap.put("void", Void.class);
    }
    private final boolean ignoreCase;
    private final Map<String, Object> nameToClassMap = new HashMap<>();

    public ClassResolver(){this(false);}
    public ClassResolver(boolean ignoreCase){this.ignoreCase = ignoreCase;}

    @Override
    public void addClasses(Map<String, Class<?>> map) {
        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            addClass(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addClassNames(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addClass(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public <C extends Collection<Class<?>>> void addClasses(C classes) {
        for (Class<?> clazz : classes) {
            addClass(clazz.getSimpleName(), clazz);
        }
    }

    @Override
    public void addClass(String name, Class<?> clazz) {
        if (ignoreCase){
            name = name.toLowerCase();
        }
        nameToClassMap.put(name, clazz);
    }

    @Override
    public void addClass(String name, String className) {
        if (ignoreCase){
            name = name.toLowerCase();
        }
        nameToClassMap.put(name, className);
    }

    @Override
    public Class<?> resolveClass(String className) {
        if(className == null){
            throw new NullPointerException("className");
        }
        if( className.length()==0){
            throw new IllegalArgumentException("className is empty");
        }
        if(ignoreCase){
            className = className.toLowerCase();
        }
        Class<?> clazz = nameToPrimitiveMap.get(className);
        if(clazz != null){
            return clazz;
        }
        Object object = nameToClassMap.get(className);
        if(object != null){
            if(object instanceof Class){
                return (Class<?>)object;
            }
            className = (String)object;
        }
        return Classes.forName(className);
    }

    @Override
    public List<Class<?>> resolveClasses(String... classNames) {
        if(classNames == null){
            throw new NullPointerException("classNames");
        }
        List<Class<?>> classes = new ArrayList<>(classNames.length);
        for (String className : classNames) {
            classes.add(resolveClass(className));
        }
        return classes;
    }
}
