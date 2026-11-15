package sandkev.differencer;

import org.junit.jupiter.api.Test;
import sandkev.differencer.api.ComparisonResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DiffSummaryTest {

    @Test
    void emptySummaryYieldsEqual() {
        DiffSummary summary = new DiffSummary();
        assertEquals(ComparisonResult.Equal, summary.getComparisonResult(),
                "Empty summary should be Equal");
        assertTrue(summary.getDiffsByType().isEmpty(),
                "getDiffsByType() should be empty");
    }

    @Test
    void singleDiffTypeReturnsThatType() {
        DiffSummary summary = new DiffSummary();
        summary.addDiff("field1", 1, 2, ComparisonResult.Changed);

        // Only one diff-type present, so getComparisonResult() == that type
        assertEquals(ComparisonResult.Changed, summary.getComparisonResult());

        Map<ComparisonResult, Map<String, List<Diff>>> map = summary.getDiffsByType();
        assertTrue(map.containsKey(ComparisonResult.Changed), "Should have only CHANGED key");

        List<Diff> diffs = map.get(ComparisonResult.Changed).get("field1");
        assertEquals(1, diffs.size());
        assertEquals(1, diffs.get(0).getExpectedValue());
        assertEquals(2, diffs.get(0).getActualValue());
    }

    @Test
    void multipleDiffTypesYieldsChanged() {
        DiffSummary summary = new DiffSummary();
        summary.addDiff("a", "foo", "bar", ComparisonResult.ApproximatelyEqual);
        summary.addDiff("b", 10, 20, ComparisonResult.Changed);

        // Two different diffTypes → overall Changed
        assertEquals(ComparisonResult.Changed, summary.getComparisonResult());

        // Both keys present
        var map = summary.getDiffsByType();
        assertTrue(map.containsKey(ComparisonResult.ApproximatelyEqual));
        assertTrue(map.containsKey(ComparisonResult.Changed));
    }

    @Test
    void changedAndApproximateFieldsResultInChanged() {
        DiffSummary summary = new DiffSummary();
        summary.addDiff("price", 1, 2, ComparisonResult.ApproximatelyEqual);
        summary.addDiff("region", "UK", "US", ComparisonResult.Changed);

        assertEquals(ComparisonResult.Changed, summary.getComparisonResult());
        assertEquals(1, summary.getDiffsByType()
                .get(ComparisonResult.ApproximatelyEqual).get("price").size());
        assertEquals(1, summary.getDiffsByType()
                .get(ComparisonResult.Changed).get("region").size());
    }

    @Test
    void getDiffsByTypeIsUnmodifiable() {
        DiffSummary summary = new DiffSummary();
        summary.addDiff("x", 0, 1, ComparisonResult.Changed);

        var map = summary.getDiffsByType();
        assertThrows(UnsupportedOperationException.class, () ->
                map.put(ComparisonResult.Equal, Map.of())
        );

        var inner = map.get(ComparisonResult.Changed);
        assertThrows(UnsupportedOperationException.class, () ->
                inner.put("y", List.of())
        );
    }

    @Test
    void equalsAndHashCode() {
        DiffSummary a = new DiffSummary();
        DiffSummary b = new DiffSummary();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.addDiff("f", 1, 2, ComparisonResult.Changed);
        // now they differ
        assertNotEquals(a, b);

        b.addDiff("f", 1, 2, ComparisonResult.Changed);
        assertEquals(a, b);
    }

    @Test
    void toStringIncludesEntriesInInsertionOrder() {
        DiffSummary summary = new DiffSummary();
        summary.addDiff("first", true, false, ComparisonResult.ApproximatelyEqual);
        summary.addDiff("second", 3.14, 2.72, ComparisonResult.Changed);

        String str = summary.toString();
        int idx1 = str.indexOf("ApproximatelyEqual");
        int idx2 = str.indexOf("Changed");
        assertTrue(idx1 < idx2,
                "toString should preserve enum‐insertion order: ApproximatelyEqual before Changed");

        assertTrue(str.contains("first"),  "toString should mention field name 'first'");
        assertTrue(str.contains("second"), "toString should mention field name 'second'");
    }
}
