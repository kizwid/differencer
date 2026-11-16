package sandkev.differencer;

import sandkev.differencer.api.ComparisonResultHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe event counters without retaining record keys or difference summaries.
 *
 * <p>Memory usage is constant with respect to the number of comparison events.
 * Like other atomic counters, {@link #reset()} is not intended to run concurrently
 * with comparison callbacks.
 */
public class CountingResultHandler<T, K> implements ComparisonResultHandler<T, K> {
    private final AtomicInteger equalCount = new AtomicInteger();
    private final AtomicInteger approximatelyEqualCount = new AtomicInteger();
    private final AtomicInteger addedCount = new AtomicInteger();
    private final AtomicInteger droppedCount = new AtomicInteger();
    private final AtomicInteger changedCount = new AtomicInteger();

    public AtomicInteger getEqualCount() {
        return equalCount;
    }

    public AtomicInteger getApproximatelyEqualCount() {
        return approximatelyEqualCount;
    }

    public AtomicInteger getAddedCount() {
        return addedCount;
    }

    public AtomicInteger getDroppedCount() {
        return droppedCount;
    }

    public AtomicInteger getChangedCount() {
        return changedCount;
    }

    @Override
    public void onEqual(K id) {
        equalCount.incrementAndGet();
    }

    @Override
    public void onApproximatelyEqual(K id, DiffSummary diff) {
        approximatelyEqualCount.incrementAndGet();
    }

    @Override
    public void onAdded(K id, T added) {
        addedCount.incrementAndGet();
    }

    @Override
    public void onDropped(K id, T dropped) {
        droppedCount.incrementAndGet();
    }

    @Override
    public void onChanged(K id, DiffSummary diff) {
        changedCount.incrementAndGet();
    }

    public void reset() {
        equalCount.set(0);
        approximatelyEqualCount.set(0);
        addedCount.set(0);
        droppedCount.set(0);
        changedCount.set(0);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "("
                + "equalCount=" + equalCount
                + ", approximatelyEqualCount=" + approximatelyEqualCount
                + ", addedCount=" + addedCount
                + ", droppedCount=" + droppedCount
                + ", changedCount=" + changedCount + ")";
    }
}
