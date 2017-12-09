package sandkev.differencer;

import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.util.Comparator;
import java.util.Iterator;


/**
 * Used to compare 2 streams of data
 * Data should ideally be sorted into a unique set using the same key comparator
 * eg records from a database ordered by primary key
 * eg files from a filesystem ordered by full path
 * @param <T> Type of object to be compared
 * @param <N> Natural key of object to  be compared
 * @param <K> Key comparator
 * @param <D> Data comparator
 */
public abstract class Differencer<T extends NaturallyKeyed<N>, N extends Serializable, K extends Comparator<N>, D extends Comparator<T>> {
    private final K keyComparator;
    private final D dataComparator;
    public Differencer(K keyComparator, D dataComparator){
        this.keyComparator = keyComparator;
        this.dataComparator = dataComparator;
    }
    public void compare(Iterator<T> expectedItor, Iterator<T> actualItor, DifferenceListener handler){

        T expected = nextOrNull(expectedItor);
        if(expected==null){
            throw new IllegalArgumentException("Stream of expected data is empty");
        }

        T actual = nextOrNull(actualItor);
        if(actual==null) {
            throw new IllegalArgumentException("Stream of actual data is empty");
        }

        while (actual!=null && expected!=null){

            int keyMatch = keyComparator.compare(actual.getKey(), expected.getKey());
            switch (keyMatch){
                case 0:
                    int match = dataComparator.compare(actual, expected);
                    if(match==0){
                        handler.onMatch(actual, expected);
                    }else {
                        handler.onDifference(actual, expected);
                    }
                    expected = nextOrNull(expectedItor);
                    break;
                case 1:
                    handler.onMissing(actual);
                    expected = nextOrNull(expectedItor);
                    break;
                case -1:
                    handler.onMissing(expected);
                    actual = nextOrNull(actualItor);
                    break;
            }

            /*
            1) we need to ensure data is sorted by primary key
               -> so we need a primaryKeyComparator
            2) we need to be able to compare the corresponding data
               -> we need and equals method that can tollerate approximatelyEqual in the case of small tolerable numberical differences

            Questions

            should we enforce the T objects to be compared to be aware of their own primaryKey (natural key)
            or is it better to provide the key separately eg in a Map.Entry key/value pair?




             */



        }


    }

    private T nextOrNull(Iterator<T> itor) {
        if(!itor.hasNext()){
            return null;
        }
        return itor.next();
    }

}
