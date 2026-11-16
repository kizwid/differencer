package sandkev.differencer;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Counts comparison events and retains unique added, dropped and changed keys.
 *
 * <p>Unlike {@link CountingResultHandler}, memory grows with the number of
 * distinct keys collected. Sets are thread-safe; exposed views cannot be
 * modified by callers. Reset is not intended to run during event processing.
 *
 * <p>The added/dropped accessors deliberately keep the legacy
 * {@code Set<Object>} return types of {@link ComparisonResultStats}.
 */
public class CollectingResultHandler<T, K> extends CountingResultHandler<T, K> {
    private final Set<K> changedKeys = ConcurrentHashMap.newKeySet();
    private final Set<Object> addedKeys = ConcurrentHashMap.newKeySet();
    private final Set<Object> droppedKeys = ConcurrentHashMap.newKeySet();

    @Override
    public void onAdded(K id, T added) {
        addedKeys.add(id);
        super.onAdded(id, added);
    }

    @Override
    public void onDropped(K id, T dropped) {
        droppedKeys.add(id);
        super.onDropped(id, dropped);
    }

    @Override
    public void onChanged(K id, DiffSummary diff) {
        changedKeys.add(id);
        super.onChanged(id, diff);
    }

    public Set<K> getChangedKeys() {
        return Collections.unmodifiableSet(changedKeys);
    }

    public Set<Object> getAddedKeys() {
        return Collections.unmodifiableSet(addedKeys);
    }

    public Set<Object> getDroppedKeys() {
        return Collections.unmodifiableSet(droppedKeys);
    }

    @Override
    public void reset() {
        super.reset();
        changedKeys.clear();
        addedKeys.clear();
        droppedKeys.clear();
    }
}
