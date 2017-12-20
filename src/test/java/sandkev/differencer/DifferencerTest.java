package sandkev.differencer;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

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
    }

    @Test(expected = IllegalArgumentException.class)
    public void willThrowExceptionIfKeysNotSorted(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("c","c"),new Data("b","b")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(3, diffs.size());
        for (Differencer.Diff<Data, String> diff : diffs) {
            assertEquals(Differencer.Diff.Type.Equal, diff.getType());
        }
    }

    @Test
    public void canMatchSame(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(3, diffs.size());
        for (Differencer.Diff<Data, String> diff : diffs) {
            assertEquals(Differencer.Diff.Type.Equal, diff.getType());
        }
    }

    @Test
    public void willWarnOnDuplicate(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("a","A"),new Data("b","b"),new Data("c","c")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(4, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Inserted, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(3).getType());
        //TODO: currently does not list the inserted duplicate key
    }

    @Test(expected = IllegalArgumentException.class)
    public void bothSidesMustBeSortedInSameDirection(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("c","c"),new Data("b","b"),new Data("a","a")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(3, diffs.size());
        for (Differencer.Diff<Data, String> diff : diffs) {
            assertEquals(Differencer.Diff.Type.Equal, diff.getType());
        }
    }

    @Test
    public void canFindDiff(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","b"),new Data("c","c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a","a"),new Data("b","B"),new Data("c","c")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(3, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Updated, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
    }

    @Test
    public void canSpotValueAddedAtEnd(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(4, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
        assertEquals(Differencer.Diff.Type.Inserted, diffs.get(3).getType());
    }

    @Test
    public void canSpotValueDeletedAtEnd(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(4, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
        assertEquals(Differencer.Diff.Type.Deleted, diffs.get(3).getType());
    }

    @Test
    public void canSpotValueAddedInMiddle(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("c"),new Data("d")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(4, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Inserted, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(3).getType());
    }

    @Test
    public void canSpotValueDeletedInMiddle(){
        Set<Data> expected = new HashSet<>(Arrays.asList(new Data("a"),new Data("b"),new Data("c"),new Data("d")));
        Set<Data> actual = new HashSet<>(Arrays.asList(new Data("a"),new Data("c"),new Data("d")));
        List<Differencer.Diff<Data,String>> diffs = differencer.compare(expected.iterator(), actual.iterator());
        assertEquals(4, diffs.size());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(0).getType());
        assertEquals(Differencer.Diff.Type.Deleted, diffs.get(1).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(2).getType());
        assertEquals(Differencer.Diff.Type.Equal, diffs.get(3).getType());
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