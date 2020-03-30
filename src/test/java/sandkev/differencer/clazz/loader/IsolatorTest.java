package sandkev.differencer.clazz.loader;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by kevin on 29/03/2020.
 */
public class IsolatorTest {


    @Test
    public void canIsolate() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        String a = String.class.newInstance();
        Foo foo = Foo.class.newInstance();

        ExampleHello hello = ExampleHello.class.newInstance();

        Collection<String> packages = Arrays.asList("sandkev.differencer.clazz.loader","sandkev.differencer.clazz.loader.IsolatorTest$ExampleInterface");
        //packages.add();
        //packages = Collections.emptyList();
        //ExampleInterface isolatedProxy = Isolator.isolate(ExampleInterface.class, ExampleHello.class, packages, new Object[0]);
        ExampleInterface isolatedProxy = Isolator.isolate(ExampleInterface.class, ExampleHello.class, packages, "Kevin");
        System.out.println(isolatedProxy.greet());
    }

    public interface ExampleInterface{
        String greet();
    }

    public static class ExampleHello implements ExampleInterface{
        private final String who;

        public ExampleHello(){
            this("world");
        }
        public ExampleHello(String who) {
            this.who = who;
        }

        @Override
        public String greet() {
            return "Hello " + who;
        }
    }

    public static  class Foo{
        String bar;

        public Foo() {
            this.bar = "bar";
        }
    }

}