package sandkev.differencer.clazz.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 29/03/2020.
 */
public class ChildFirstClassLoader extends URLClassLoader{
    private static final Logger log = LoggerFactory.getLogger(ChildFirstClassLoader.class);
    private final Map<String, URL> loadedResources = new HashMap<>();
    private final Collection<String> isolatedClassNames;
    public ChildFirstClassLoader(Collection<String> isolatedClassNames) {
        super(((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs(), Thread.currentThread().getContextClassLoader());
        this.isolatedClassNames = isolatedClassNames;
        log.trace("Isolated classes: {}. URLs: {}", isolatedClassNames, Arrays.asList(super.getURLs()));
    }
    private boolean isIsolated(String className){
        if(isolatedClassNames.isEmpty()){
            return true;
        }else {
            for (String isolatedClassName : isolatedClassNames) {
                if(className.contains(isolatedClassName)){
                    return true;
                }
            }
        }
        return false;
    }
    public synchronized URL getResource(final String name){
        URL loadResource = null;
        if(isIsolated(name)) {
            loadResource = loadedResources.get(name);
            if (loadResource == null) {
                loadResource = findResource(name);
                if (loadResource == null) {
                    log.trace("[NON-ISOLATED RESOURCE despite desiring isolation] Loaded resource: {}", name);
                    loadResource = super.getResource(name);
                } else {
                    loadedResources.put(name, loadResource);
                    log.trace("[ISOLATED RESOURCE (2nd attempt)] Loaded resource: {}", name);
                }
            } else {
                log.trace("[ISOLATED RESOURCE] Loaded resource: {}", name);
            }
        }else {
            loadResource = super.getResource(name);
            log.trace("[NON-ISOLATED RESOURCE] Loaded resource: {}", name);
        }
        log.trace("Loaded {} resource(s) so far: {}", loadedResources.size(), loadedResources.keySet());
        return loadResource;
    }
    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolveClass)
            throws ClassNotFoundException {
        Class<?> loadedClass = null;
        if(isIsolated(name)) {
            loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                try {
                    loadedClass = findClass(name);
                    log.trace("[ISOLATED] Loaded class: {}", name);
                } catch (ClassNotFoundException e) {
                    loadedClass = getParent().loadClass(name);
                    log.trace("[NON-ISOLATED despite desiring isolation] Loaded class: {}", name);
                } catch (SecurityException e) {
                    throw new RuntimeException("Cant load class " + name, e);
                }
            } else {
                log.trace("[ISOLATED (alternative code path)] Loaded class: {}", name);
            }
            if (resolveClass) {
                log.trace("Linking class: {}", name);
                resolveClass(loadedClass);
            }
        }else {
            loadedClass = getParent().loadClass(name);
            log.debug("[NON-ISOLATED] class: {}", name);
        }
        return loadedClass;
    }
}
