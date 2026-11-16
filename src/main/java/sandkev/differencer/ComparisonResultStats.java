package sandkev.differencer;

/**
 * Legacy collecting result handler, retained for source and binary compatibility.
 *
 * <p>Use {@link CountingResultHandler} for constant-memory counting, or
 * {@link CollectingResultHandler} when the affected keys are required.
 */
public class ComparisonResultStats<T, K> extends CollectingResultHandler<T, K> {
}
