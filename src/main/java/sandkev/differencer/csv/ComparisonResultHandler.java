package sandkev.differencer.csv;

/**
 * callback for comparison events.
 */
public interface ComparisonResultHandler<K,T> {
    void onEqual(K naturalKey, T expected, T actual, String context);
    void onApproximatelyEqual(K naturalKey, T expected, T acual, String context);
    void onInserted(K naturalKey, T acual, String context);
    void onDeleted(K naturalKey, T expected, String context);
    void onChanged(K naturalKey, T expected, T actual, String context);
}
