package sandkev.differencer.csv;

import sandkev.differencer.NaturallyKeyed;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by kevin on 01/10/2018.
 * @param <T> Type of object to be compared
 * @param <K> Natural key of object to  be compared
 * @param <C> Key comparator
 * @param <D> Data comparator
 */
public interface DiffAlgorithm<T extends NaturallyKeyed<K>, K extends Serializable, C extends Comparator<K>, D extends Comparator<T>> {
    void computeDiff(Iterable<T> original, Iterable<T> revision, ComparisonResultHandler handler);
}
