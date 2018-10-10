package sandkev.differencer.csv;

import sandkev.differencer.NaturallyKeyed;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedSet;

/**
 * Created by kevin on 08/10/2018.
 */
public class SortedSetDiffAlgorithm<T extends NaturallyKeyed<K>, K extends Serializable, C extends Comparator<K>, D extends Comparator<T>>
 implements DiffAlgorithm<T, K, C ,D>{

    double tollerence = 0;
    C keyComparator;
    D dataComparator;

    public SortedSetDiffAlgorithm(C keyComparator, D dataComparator) {
        this.keyComparator = keyComparator;
        this.dataComparator = dataComparator;
    }

/*
    public void computeDiff(SortedSet<T> original, SortedSet<T> revision, ComparisonResultHandler handler) {
        validateComparitors(original, revision);
        computeDiff(original, revision, handler);
    }

    private void validateComparitors(SortedSet<T> original, SortedSet<T> revision) {
        Comparator originalComparitor = original.comparator();
        Comparator revisionComparitor = original.comparator();
        if(originalComparitor != null && !originalComparitor.equals(revisionComparitor)){
            throw new IllegalArgumentException("Comparitors for the two sets must be the same");
        }
    }
*/


    public void computeDiff(Iterable<T> originalIterable, Iterable<T> revisionIterable, ComparisonResultHandler handler) {

        Iterator<T> originalIterator = originalIterable.iterator();
        Optional<T> originalOptional = next(originalIterator);
        if(!originalOptional.isPresent()){
            throw new IllegalArgumentException("Stream of expected data is empty");
        }

        Iterator<T> revisionIterator = revisionIterable.iterator();
        Optional<T> revisionOptional = next(revisionIterator);
        if(!revisionOptional.isPresent()) {
            throw new IllegalArgumentException("Stream of actual data is empty");
        }

        T previousOriginal = null;
        T previousRevision = null;
        int previousDirection = 0;
        while (originalOptional.isPresent() && revisionOptional.isPresent()){

            T orginal = originalOptional.get();
            T revision = revisionOptional.get();

            int direction=0;
            if(previousOriginal!=null){
                direction=keyComparator.compare(previousOriginal.getNaturalKey(), orginal.getNaturalKey());
                if(direction==0){
                    System.out.println("duplicate found: " + orginal.getNaturalKey());
                }
            }

            int keyMatch = keyComparator.compare(orginal.getNaturalKey(), revision.getNaturalKey());
            if(keyMatch == 0){
                //same key
                int match = dataComparator.compare(orginal, revision);
                if(match==0){
                    handler.onEqual(orginal.getNaturalKey(), orginal, revision, "");
                }else {
                    if(tollerence > 0){
                        if(approximatelyEquals(orginal, revision, tollerence)){
                            handler.onApproximatelyEqual(orginal.getNaturalKey(), orginal, revision, "");
                        }else {
                            handler.onChanged(orginal.getNaturalKey(), orginal, revision, "");
                        }
                    }else {
                        handler.onChanged(orginal.getNaturalKey(), orginal, revision, "");
                    }
                }
                originalOptional = next(originalIterator);
                revisionOptional = next(revisionIterator);

            }else if( keyMatch > 0){
                //original is bigger
                handler.onInserted(revision.getNaturalKey(), revision, "");
                revisionOptional = next(revisionIterator);
            }else {
                //revision is bigger
                handler.onDeleted(orginal.getNaturalKey(), orginal, "");
                originalOptional = next(originalIterator);
            }



        }


    }

    private boolean approximatelyEquals(T orginal, T revision, double tollerence) {
        return false;
    }

    private Optional<T> next(Iterator<T> itor) {
        return itor==null||!itor.hasNext()?Optional.<T>empty():Optional.ofNullable(itor.next());
    }


}
