package sandkev.differencer;

import org.junit.jupiter.api.Test;
import sandkev.differencer.api.Identifiable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ValidatingIterableTest {

    // A simple Identifiable implementation for testing extractId()
    private static class Person implements Identifiable<Integer> {
        private final int id;
        Person(int id) { this.id = id; }
        @Override public Integer getId() { return id; }
        @Override public String toString() { return "Person#" + id; }
    }

    @Test
    void iteratesInOrder_whenSorted() {
        List<Integer> sorted = Arrays.asList(1, 2, 3, 5, 8);
        ValidatingIterable<Integer> vif =
                new ValidatingIterable<>(sorted, Comparator.naturalOrder());

        // Directly compare the Iterable to the expected list
        assertIterableEquals(sorted, vif);
    }

    @Test
    void handlesEmptyIterableGracefully() {
        ValidatingIterable<Integer> vif =
                new ValidatingIterable<Integer>(Collections.emptyList(), Comparator.naturalOrder());

        // Should simply produce no elements
        assertIterableEquals(Collections.emptyList(), vif);
    }

    @Test
    void handlesSingleElement() {
        List<Integer> single = Collections.singletonList(42);
        ValidatingIterable<Integer> vif =
                new ValidatingIterable<>(single, Comparator.naturalOrder());

        assertIterableEquals(single, vif);
    }

    @Test
    void throwsOnDuplicate() {
        List<Integer> dupes = Arrays.asList(1, 2, 2, 3);
        ValidatingIterable<Integer> vif =
                new ValidatingIterable<>(dupes, Comparator.naturalOrder());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vif.iterator().forEachRemaining(i -> {})
        );
        assertTrue(ex.getMessage().contains("Duplicate key detected: <2>"));
    }

    @Test
    void throwsOnOutOfOrder() {
        List<Integer> bad = Arrays.asList(1, 4, 3, 5);
        ValidatingIterable<Integer> vif =
                new ValidatingIterable<>(bad, Comparator.naturalOrder());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vif.iterator().forEachRemaining(i -> {})
        );
        assertTrue(ex.getMessage().startsWith("Out of order:"));
        // Optionally check IDs are reversed in the message
        assertTrue(ex.getMessage().contains("<3>") && ex.getMessage().contains("<4>"));
    }

    @Test
    void extractId_usesIdentifiable() {
        List<Person> people = Arrays.asList(new Person(10), new Person(10));
        ValidatingIterable<Person> vif =
                new ValidatingIterable<>(people, Comparator.comparing(Person::getId));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> vif.iterator().forEachRemaining(p -> {})
        );
        // should mention the duplicate ID “10”
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    void constructorNullChecks() {
        assertThrows(NullPointerException.class,
                () -> new ValidatingIterable<>(null, Comparator.naturalOrder()),
                "source must not be null"
        );
        assertThrows(NullPointerException.class,
                () -> new ValidatingIterable<>(List.of(), null),
                "comparator must not be null"
        );
    }
}
