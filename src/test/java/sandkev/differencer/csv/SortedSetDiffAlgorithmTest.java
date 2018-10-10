package sandkev.differencer.csv;

import junit.framework.TestCase;
import org.junit.Test;
import sandkev.differencer.NaturallyKeyed;

import java.util.Comparator;
import java.util.TreeSet;
import lombok.Builder;
import lombok.Data;

/**
 * Created by kevin on 08/10/2018.
 */
public class SortedSetDiffAlgorithmTest {

    @Data
    @Builder
    private static class MyType implements NaturallyKeyed<String>{
        String naturalKey;
        Number importantValue;
        String otherInterestingField;
        String ignorableField;
    }

    @Test
    public void canCompareMatchedSortedSet(){

        Comparator<String> keyComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        };
        Comparator<MyType> myKeyComparator = new Comparator<MyType>() {
            @Override
            public int compare(MyType o1, MyType o2) {
                return keyComparator.compare(o1.getNaturalKey(), o2.getNaturalKey());
            }
        };
        Comparator<MyType> dataComparator = new Comparator<MyType>() {
            @Override
            public int compare(MyType o1, MyType o2) {
                int n = o1.getNaturalKey().compareToIgnoreCase(o2.getNaturalKey());
                if(n==0){
                    n = Double.valueOf(o1.getImportantValue().doubleValue()).compareTo(
                            Double.valueOf(o2.importantValue.doubleValue()));
                }
                return n;
            }
        };

        TreeSet<MyType> originals = new TreeSet<>(myKeyComparator);
//        originals.add(MyType.builder().naturalKey("a").importantValue(10).otherInterestingField("foo").ignorableField("aaa").build());
        originals.add(MyType.builder().naturalKey("b").importantValue(20).otherInterestingField("foo").ignorableField("aaa").build());
        originals.add(MyType.builder().naturalKey("c").importantValue(30).otherInterestingField("foo").ignorableField("aaa").build());

        TreeSet<MyType> revisions = new TreeSet<>(myKeyComparator);
        revisions.add(MyType.builder().naturalKey("a").importantValue(10).otherInterestingField("foo").ignorableField("aaa").build());
        revisions.add(MyType.builder().naturalKey("b").importantValue(20).otherInterestingField("foo").ignorableField("aaa").build());
        revisions.add(MyType.builder().naturalKey("c").importantValue(31).otherInterestingField("foo").ignorableField("aaa").build());
        revisions.add(MyType.builder().naturalKey("d").importantValue(40).otherInterestingField("foo").ignorableField("aaa").build());


        SortedSetDiffAlgorithm diffAlgorithm = new SortedSetDiffAlgorithm<MyType, String, Comparator<String>, Comparator<MyType>>(keyComparator, dataComparator);
        ComparisonResultHandler handler = new ComparisonResultHandler() {
            @Override
            public void onEqual(Object naturalKey, Object expected, Object actual, String context) {
                System.out.println("equals: " + expected);
            }

            @Override
            public void onApproximatelyEqual(Object naturalKey, Object expected, Object acual, String context) {

            }

            @Override
            public void onInserted(Object naturalKey, Object actual, String context) {
                System.out.println("inserted: " + actual);
            }

            @Override
            public void onDeleted(Object naturalKey, Object expected, String context) {
                System.out.println("deleted: " + expected);

            }

            @Override
            public void onChanged(Object naturalKey, Object expected, Object actual, String context) {
                System.out.println("changed: " + expected + " -> " + actual);

            }
        };
        diffAlgorithm.computeDiff(originals, revisions, handler);



//        NaturallyKeyed<>
//
//        TreeSet<>


    }

}