package sandkev.differencer.clazz.loader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kevin on 29/03/2020.
 */
public class IsolatedInvocationHandler implements InvocationHandler {

    private final Object proxyInstance;
    private final ClassLoader injectedClassLoader;

    public IsolatedInvocationHandler(Object proxyInstance, ClassLoader injectedClassLoader){
        this.proxyInstance = proxyInstance;
        this.injectedClassLoader = injectedClassLoader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(injectedClassLoader);
            return method.invoke(proxyInstance, args);
        }finally {
            Thread.currentThread().setContextClassLoader(originalLoader);
        }
    }
    public static Object createIsolatedProxy(ClassLoader classLoader, Object wrappedInstance, Class<?>[] interfaces){
        InvocationHandler handler = new IsolatedInvocationHandler(wrappedInstance, classLoader);
        return Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
