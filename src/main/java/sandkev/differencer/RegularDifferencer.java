package sandkev.differencer;

import sandkev.differencer.api.ComparisonResultHandler;
import sandkev.differencer.api.DiffAlgorithm;
import sandkev.differencer.api.DiffComparator;
import sandkev.differencer.api.Identifiable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;


/**
 * A one-pass differ for two sorted, duplicate-free streams of Identifiable<T>.
 *
 * <p><strong>Preconditions:</strong> These are required even when runtime validation is disabled.
 * <ul>
 *   <li>Each source Iterable must be sorted strictly by the provided keyComparator.</li>
 *   <li>No two items in a source may be “equal” under that same comparator.</li>
 *   <li>The key comparator must identify the same logical key as {@link Identifiable#getId()}.</li>
 * </ul>
 */
public class RegularDifferencer<T extends Identifiable<K>,K>
  implements DiffAlgorithm<T,K> {

    private final Comparator<? super T> keyComparator;
    private final DiffComparator<? super T> dataComparator;
    /**
     * Enables an optional single-pass check of the sorted, unique key precondition.
     * Disabled by default for callers who already guarantee valid inputs.
     */
    private final boolean validateInputs;

    public RegularDifferencer(Comparator<? super T> keyComparator,
                              DiffComparator<? super T> dataComparator) {
        this(keyComparator, dataComparator, false);
    }
    /**
     * @param keyComparator   used to order and identify equality of T instances
     * @param dataComparator  used to compute field‑level diffs once keys match
     * @param validateInputs  if true, wrap inputs in a ValidatingIterable to enforce sortedness & uniqueness
     */
    public RegularDifferencer(Comparator<? super T> keyComparator,
                              DiffComparator<? super T> dataComparator, boolean validateInputs) {
        this.keyComparator  = requireNonNull(keyComparator);
        this.dataComparator = requireNonNull(dataComparator);
        this.validateInputs = validateInputs;
    }

    public static <T extends Identifiable<K>,K> RegularDifferencer<T,K> withValidation(Comparator<? super T> keyComparator,
                                                                                       DiffComparator<? super T> dataComparator) {
       return new RegularDifferencer<T,K>(keyComparator, dataComparator, true);
    }
    public static <T extends Identifiable<K>,K> RegularDifferencer<T,K> withoutValidation(Comparator<? super T> keyComparator,
                                                                                          DiffComparator<? super T> dataComparator) {
        return new RegularDifferencer<T,K>(keyComparator, dataComparator, false);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if validation is on and inputs are out‑of‑order or contain duplicates
     */
    @Override
    public void computeDiff(Iterable<T> expected,
                            Iterable<T> actual,
                            ComparisonResultHandler<T,K> handler) {

        requireNonNull(expected, "expected iterable must not be null");
        requireNonNull(actual,   "actual iterable must not be null");
        requireNonNull(handler,  "handler must not be null");

        //optionally decorate the iterators with validation that they are sorted sets
        Iterable<T> expectedIterable = validateInputs
                ? new ValidatingIterable<>(expected, keyComparator)
                : expected;
        Iterable<T> actualIterable   = validateInputs
                ? new ValidatingIterable<>(actual,   keyComparator)
                : actual;
        final Iterator<T> itE = expectedIterable.iterator();
        final Iterator<T> itA = actualIterable.iterator();

        T e = poll(itE), a = poll(itA);

        while (e != null && a != null) {
            int cmp = keyComparator.compare(a, e);
            if (cmp == 0) {
                handleMatch(handler, e, a);
                a = poll(itA);
                e = poll(itE);
            } else if (cmp < 0) {
                handler.onAdded(a.getId(), a);
                a = poll(itA);
            } else {
                handler.onDropped(e.getId(), e);
                e = poll(itE);
            }
        }

        // flush remaining
        if (e != null) {
            handler.onDropped(e.getId(), e);
            itE.forEachRemaining(drop(handler));
        }
        if (a != null) {
            handler.onAdded(a.getId(), a);
            itA.forEachRemaining(add(handler));
        }
    }

    private void handleMatch(ComparisonResultHandler<T, K> handler, T e, T a) {
        // The comparator contract and DiffSummary both use expected, then actual.
        DiffSummary d = requireNonNull(dataComparator.compare(e, a), "data comparator must return a DiffSummary");
        switch (d.getComparisonResult()) {
            case Equal -> handler.onEqual(e.getId());
            case ApproximatelyEqual -> handler.onApproximatelyEqual(e.getId(), d);
            case Changed -> handler.onChanged(e.getId(), d);
        }
    }

    private static <T> T poll(Iterator<T> it) {
        return it.hasNext() ? it.next() : null;
    }
    private Consumer<T> drop(ComparisonResultHandler<T,K> h ) {
        return rem -> h.onDropped(rem.getId(), rem);
    }
    private Consumer<T> add( ComparisonResultHandler<T,K> h ) {
        return rem -> h.onAdded(rem.getId(), rem);
    }

    boolean isValidationOn() {
        return validateInputs;
    }
}
