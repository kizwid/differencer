package sandkev.differencer.api;

import sandkev.differencer.ComparisonResultStats;
import sandkev.differencer.CountingResultHandler;

import java.util.Objects;

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
     * Counts comparison outcomes without storing affected keys.
     */
    default CountingResultHandler<T, K> diffAndCount(Iterable<T> expected,
                                                     Iterable<T> actual) {
        CountingResultHandler<T, K> counts = new CountingResultHandler<>();
        computeDiff(expected, actual, counts);
        return counts;
    }

    /**
     * Runs the comparison and collects unique added, dropped and changed keys
     * as well as counts. Retains {@link ComparisonResultStats} as the return
     * type for compatibility; use {@link #diffAndCount(Iterable, Iterable)}
     * when only constant-memory counts are required.
     */
    default ComparisonResultStats<T, K> diffAndCollect(Iterable<T> expected,
                                                       Iterable<T> actual) {
        ComparisonResultStats<T, K> stats = new ComparisonResultStats<>();
        computeDiff(expected, actual, stats);
        return stats;
    }
}
