# Differencer

A one-pass Java 21 library for comparing **two sorted, unique-key data sources**.
It reports equal, approximately equal, changed, added, and dropped records via
callbacks. The inputs can be streaming `Iterable` sources; they do not need
to be materialised as sets.

## Example

```java
import sandkev.differencer.DiffSummary;
import sandkev.differencer.RegularDifferencer;
import sandkev.differencer.api.ComparisonResult;
import sandkev.differencer.api.Identifiable;

import java.util.Comparator;
import java.util.List;

record Account(long id, String name) implements Identifiable<Long> {
    @Override public Long getId() { return id; }
}

var expected = List.of(new Account(1, "Alice"), new Account(2, "Bob"));
var actual = List.of(new Account(1, "Alicia"), new Account(3, "Charlie"));

var differencer = RegularDifferencer.withValidation(
    Comparator.comparingLong(Account::id),
    (Account baseline, Account observed) -> {
        var summary = new DiffSummary();
        if (!baseline.name().equals(observed.name())) {
            summary.addDiff("name", baseline.name(), observed.name(),
                            ComparisonResult.Changed);
        }
        return summary;
    }
);

var result = differencer.diffAndCollect(expected, actual);

System.out.println(result.getChangedKeys());  // [1]
System.out.println(result.getDroppedKeys());  // [2]
System.out.println(result.getAddedKeys());    // [3]
```

A comparator receives **expected first, actual second**. When the keys match,
an empty `DiffSummary` means `Equal`; a summary containing only
`ApproximatelyEqual` field differences means `ApproximatelyEqual`;
any `Changed` field difference means `Changed`.

## Choosing a result handler

- `diffAndCount(expected, actual)` counts outcomes without retaining keys;
  memory use is constant with respect to the number of differences.
- `diffAndCollect(expected, actual)` returns `ComparisonResultStats` with
  outcome counts and distinct added, dropped, and changed keys.
- `computeDiff(expected, actual, handler)` streams events directly to a
  custom `ComparisonResultHandler`.

## Input contract

Both sources must be strictly sorted by the **same key comparator** and have
no duplicate keys. The comparator's definition of key equality must agree with
`Identifiable.getId()`. Validation is **opt-in**: use
`RegularDifferencer.withValidation(...)` to detect unsorted or duplicate keys
during iteration. The two-argument constructor and `withoutValidation(...)`
avoid validation checks for trusted inputs; they do **not** sort inputs.

`DiffSummary` is mutable while being built, but `getDiffsByType()` returns an
unmodifiable, detached snapshot. The contained `Diff` instances are immutable;
values of type `Object` can still refer to mutable application objects.

## Build and test

Requires Java 21:

```bash
./gradlew clean test
```

The existing `readme.txt` contains historical design notes and experiments.
