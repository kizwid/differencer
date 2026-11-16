package sandkev.differencer;

import org.junit.jupiter.api.Test;
import sandkev.differencer.api.ComparisonResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResultHandlersTest {

    @Test
    void countingHandlerCountsAllOutcomesWithoutRetainingKeys() {
        CountingResultHandler<String, Integer> counts = new CountingResultHandler<>();
        counts.onEqual(1);
        counts.onApproximatelyEqual(2, new DiffSummary());
        counts.onAdded(3, "new");
        counts.onDropped(4, "old");
        counts.onChanged(5, new DiffSummary());
        counts.onAdded(3, "new again");

        assertEquals(1, counts.getEqualCount().get());
        assertEquals(1, counts.getApproximatelyEqualCount().get());
        assertEquals(2, counts.getAddedCount().get());
        assertEquals(1, counts.getDroppedCount().get());
        assertEquals(1, counts.getChangedCount().get());

        counts.reset();
        assertEquals(0, counts.getEqualCount().get());
        assertEquals(0, counts.getApproximatelyEqualCount().get());
        assertEquals(0, counts.getAddedCount().get());
        assertEquals(0, counts.getDroppedCount().get());
        assertEquals(0, counts.getChangedCount().get());
    }

    @Test
    void collectingHandlerCountsEventsAndStoresUniqueKeys() {
        CollectingResultHandler<String, Integer> result = new CollectingResultHandler<>();
        DiffSummary difference = new DiffSummary();
        difference.addDiff("field", "before", "after", ComparisonResult.Changed);

        result.onEqual(1);
        result.onApproximatelyEqual(2, difference);
        result.onAdded(3, "a");
        result.onAdded(3, "again");
        result.onDropped(4, "b");
        result.onChanged(5, difference);

        assertEquals(1, result.getEqualCount().get());
        assertEquals(1, result.getApproximatelyEqualCount().get());
        assertEquals(2, result.getAddedCount().get());
        assertEquals(1, result.getDroppedCount().get());
        assertEquals(1, result.getChangedCount().get());
        assertEquals(Set.of(3), result.getAddedKeys());
        assertEquals(Set.of(4), result.getDroppedKeys());
        assertEquals(Set.of(5), result.getChangedKeys());
        assertThrows(UnsupportedOperationException.class, () -> result.getAddedKeys().add(99));

        result.reset();
        assertTrue(result.getAddedKeys().isEmpty());
        assertTrue(result.getDroppedKeys().isEmpty());
        assertTrue(result.getChangedKeys().isEmpty());
        assertEquals(0, result.getChangedCount().get());
    }

    @Test
    void comparisonResultStatsRetainsLegacyAccessorsAndBehaviour() {
        ComparisonResultStats<String, Integer> legacy = new ComparisonResultStats<>();
        legacy.onAdded(10, "new");
        legacy.onDropped(20, "old");
        legacy.onChanged(30, new DiffSummary());

        Set<Object> added = legacy.getAddedKeys();
        Set<Object> dropped = legacy.getDroppedKeys();
        Set<Integer> changed = legacy.getChangedKeys();

        assertEquals(Set.of(10), added);
        assertEquals(Set.of(20), dropped);
        assertEquals(Set.of(30), changed);
        assertEquals(1, legacy.getAddedCount().get());
        assertTrue(legacy.toString().startsWith("ComparisonResultStats("));
    }
}
