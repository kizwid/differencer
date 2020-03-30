package sandkev.differencer.clazz;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 29/03/2020.
 */
public interface IClassResolver {
    void addClasses(Map<String, Class<?>> map);
    void addClassNames(Map<String, String> map);
    <C extends Collection<Class<?>>> void addClasses(C classes);
    void addClass(String name, Class<?> clazz);
    void addClass(String name, String className);
    Class<?> resolveClass(String className);
    List<Class<?>> resolveClasses(String... classNames);
}
