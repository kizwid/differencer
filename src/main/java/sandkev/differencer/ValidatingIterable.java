package sandkev.differencer;

import sandkev.differencer.api.Identifiable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * Wraps an existing Iterable<T> and, as you iterate, asserts that:
 *  1) keys are strictly increasing (i.e. comparator.compare(prev,cur) < 0)
 *  2) no duplicates (i.e. comparator.compare(prev,cur) != 0)
 *
 * Throws IllegalArgumentException at first out‑of‑order or duplicate.
 */
public class ValidatingIterable<T> implements Iterable<T> {
    private final Iterable<T> source;
    private final Comparator<? super T> comparator;

    public ValidatingIterable(Iterable<T> source, Comparator<? super T> comparator) {
        this.source = Objects.requireNonNull(source, "source must not be null");
        this.comparator = Objects.requireNonNull(comparator, "comparator must not be null");
    }

    @Override
    public Iterator<T> iterator() {
        return new ValidatingIterator<>(source.iterator(), comparator);
    }

    private static class ValidatingIterator<T> implements Iterator<T> {
        private final Iterator<T> inner;
        private final Comparator<? super T> comparator;
        private T previous;
        private boolean hasPrevious = false;

        ValidatingIterator(Iterator<T> inner, Comparator<? super T> comparator) {
            this.inner = inner;
            this.comparator = comparator;
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public T next() {
            T current = inner.next();
            if (hasPrevious) {
                int cmp = comparator.compare(previous, current);
                if (cmp > 0) {
                    throw new IllegalArgumentException(String.format(
                            "Out of order: key <%s> should come before <%s>",
                            extractId(current), extractId(previous)
                    ));
                }
                if (cmp == 0) {
                    throw new IllegalArgumentException(
                            String.format("Duplicate key detected: <%s>", extractId(current))
                    );
                }
            }
            previous = current;
            hasPrevious = true;
            return current;
        }

        /**
         * Helper to pull out an identifier for logging;
         * you can adapt this to your Identifiable interface.
         */
        private String extractId(T item) {
            if (item instanceof Identifiable) {
                return String.valueOf(((Identifiable<?>) item).getId());
            }
            return item.toString();
        }
    }
}

