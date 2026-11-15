package sandkev.differencer;

import sandkev.differencer.api.ComparisonResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Container for collection of differences.
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
