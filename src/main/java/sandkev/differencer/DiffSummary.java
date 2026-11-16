package sandkev.differencer;

import sandkev.differencer.api.ComparisonResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A mutable accumulator for field differences.
 *
 * <p>{@link #getDiffsByType()} returns a detached, unmodifiable snapshot.
 * The contained {@link Diff} objects are shallowly immutable, but their
 * expected and actual values may themselves be mutable objects.
 *
 * <p>Build summaries on a single thread; concurrent mutation is not supported.
 */
public class DiffSummary {
  private final EnumMap<ComparisonResult, LinkedHashMap<String,List<Diff>>> diffsByType =
      new EnumMap<>(ComparisonResult.class);

  /**
   * Records a field difference with baseline/expected value first and observed/actual value second.
   */
  public void addDiff(String fieldName,
                      Object expectedValue,
                      Object actualValue,
                      ComparisonResult diffType) {
    Objects.requireNonNull(fieldName, "fieldName must not be null");
    Objects.requireNonNull(diffType, "diffType must not be null");
    if (diffType != ComparisonResult.Changed && diffType != ComparisonResult.ApproximatelyEqual) {
      throw new IllegalArgumentException("Field differences must be Changed or ApproximatelyEqual: " + diffType);
    }

    var fieldMap = diffsByType
      .computeIfAbsent(diffType, dt -> new LinkedHashMap<>());
    var list = fieldMap
      .computeIfAbsent(fieldName, fn -> new ArrayList<>());
    list.add(Diff.builder()
                .expectedValue(expectedValue)
                .actualValue(actualValue)
                .build());
  }

  public ComparisonResult getComparisonResult() {
    if (diffsByType.isEmpty()) return ComparisonResult.Equal;
    var types = diffsByType.keySet();
    return types.size() == 1
        ? types.iterator().next()
        : ComparisonResult.Changed;
  }

  public Map<ComparisonResult, Map<String,List<Diff>>> getDiffsByType() {
    // 1) Build a new outer map (EnumMap for efficiency)
    Map<ComparisonResult, Map<String,List<Diff>>> snapshot =
            new EnumMap<>(ComparisonResult.class);

    // 2) For each diff‐type, build an unmodifiable inner map
    for (var entry : diffsByType.entrySet()) {
      ComparisonResult type = entry.getKey();

      // Use LinkedHashMap to preserve your insertion order
      Map<String,List<Diff>> inner = new LinkedHashMap<>();

      // 3) For each fieldName → List<Diff>, copy and wrap the list
      for (var fieldEntry : entry.getValue().entrySet()) {
        // Copy the List so future adds to your internal map
        // won't show up here
        List<Diff> listCopy = new ArrayList<>(fieldEntry.getValue());
        inner.put(fieldEntry.getKey(), Collections.unmodifiableList(listCopy));
      }

      // Wrap the entire inner map
      snapshot.put(type, Collections.unmodifiableMap(inner));
    }

    // 4) Finally, wrap the outer map
    return Collections.unmodifiableMap(snapshot);
  }


  @Override
  public String toString() {
    return diffsByType.entrySet().stream()
      .map(e -> e.getKey() + "=" + e.getValue())
      .collect(Collectors.joining(", "));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiffSummary that = (DiffSummary) o;
    return Objects.equals(diffsByType, that.diffsByType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diffsByType);
  }
}
