package sandkev.differencer.api;

import sandkev.differencer.ComparisonResultStats;

/**
 * A functional interface that computes the diff between two
 * sorted, duplicate-free streams of Identifiable<K> items.
 *
 * @param <T> the element type, which must expose a primary key of type K
 * @param <K> the type of the primary key
 */
@FunctionalInterface
public interface DiffAlgorithm<T extends Identifiable<K>, K> {

    /**
     * Walks two sorted, duplicate-free iterables in one pass and fires
     * callback events for equals, additions, drops and changes.
     *
     * @param expected        the “baseline” stream, sorted and without duplicates
     * @param actual          the “new”   stream,   sorted and without duplicates
     * @param handler         where to send your onEqual/onAdded/onDropped/onChanged events
     * @throws NullPointerException     if any argument is null
     * @throws IllegalArgumentException if input validation is enabled and inputs are not sorted or contain duplicates
     */
    void computeDiff(Iterable<T>                expected,
                     Iterable<T>                actual,
                     ComparisonResultHandler<T,K> handler);

    /**
     * Convenience method: runs the diff and returns a ComparisonResultStats
     * so you don’t have to wire up a handler yourself.
     */
    default ComparisonResultStats diffAndCollect(Iterable<T> expected,
                                                 Iterable<T> actual) {
        ComparisonResultStats stats = new ComparisonResultStats();
        computeDiff(expected, actual, stats);
        return stats;
    }
}
