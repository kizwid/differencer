package sandkev.differencer;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kevsa on 09/12/2017.
 */
public class DifferencerTest {

    private Comparator<String> keyComparator;
    private Comparator<Data> dataComparator;
    private Differencer differencer;
    private DifferenceListener handler;

    @Before
    public void setUp() throws Exception {
        keyComparator = new Comparator<String>() {
            @Override
            public int compare(String a, String e) {
                return a.compareTo(e);
            }
        };
        dataComparator = new Comparator<Data>() {
            @Override
            public int compare(Data a, Data e){
                return a.toString().compareTo(e.toString());
            }
        };
        differencer = new Differencer(keyComparator, dataComparator) {
            @Override
            public void compare(Iterator expectedItor, Iterator actualItor, DifferenceListener handler) {
                super.compare(expectedItor, actualItor, handler);
            }
        };
        handler = new DifferenceListener<Data>() {
            @Override
            public void onMatch(Data actual, Data expected) {
                System.out.println("matched " + actual + "=" + expected);
            }

            @Override
            public void onDifference(Data actual, Data expected) {
                System.out.println("diff " + actual + "=" + expected);
            }

            @Override
            public void onMissing(Data expected) {
                System.out.println("missing " + expected);
            }

            @Override
            public void onAdded(Data actual) {
                System.out.println("added " + actual);
            }
        };
    }

    @Test
    public void canMatchSame(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c")));
        differencer.compare(expected.iterator(),actual.iterator(), handler);
    }

    @Test
    public void canSpotValueAddedAtEnd(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        differencer.compare(expected.iterator(),actual.iterator(), handler);
    }

    @Test
    public void canSpotValueAddedInMiddle(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("c"),new Data("d")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        differencer.compare(expected.iterator(),actual.iterator(), handler);
    }

    private static class Data implements NaturallyKeyed<String>{
        private final String value;
        private final String Key;

        public Data(String Key) {
            this.Key = Key;
            this.value = Key;
        }
        public Data(String key, String value) {
            this.Key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return Key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            if (value != null ? !value.equals(data.value) : data.value != null) return false;
            return Key != null ? Key.equals(data.Key) : data.Key == null;

        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (Key != null ? Key.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "value='" + value + '\'' +
                    ", Key='" + Key + '\'' +
                    '}';
        }
    }


}