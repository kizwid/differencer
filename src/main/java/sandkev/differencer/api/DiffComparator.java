package sandkev.differencer.api;

import sandkev.differencer.DiffSummary;

/**
 * Compares matching-key records, producing a field-level summary.
 *
 * <p>The first argument is the baseline/expected record, and the second is
 * the observed/actual record. Implementations should pass field values to
 * {@link DiffSummary#addDiff(String, Object, Object, ComparisonResult)}
 * in that same order.
 *
 * @param <T> record type
 */
@FunctionalInterface
public interface DiffComparator<T> {
    DiffSummary compare(T expected, T actual);
}
