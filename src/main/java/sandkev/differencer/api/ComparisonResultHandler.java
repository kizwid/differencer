package sandkev.differencer.api;

import sandkev.differencer.DiffSummary;

/**
 * callback for comparison events.
 */
public interface ComparisonResultHandler<T,K> {
    void onEqual(K id);
    void onApproximatelyEqual(K id, DiffSummary diff);
    void onAdded(K id, T added);
    void onDropped(K id, T dropped);
    void onChanged(K id, DiffSummary diff);
}
