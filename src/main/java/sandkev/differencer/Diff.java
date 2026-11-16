package sandkev.differencer;

import lombok.Builder;
import lombok.Value;

/**
 * A pair of field values, in expected/actual order.
 *
 * <p>Diff is a shallowly immutable value object. Values themselves are of type
 * Object; callers must ensure those values are immutable (or use defensive
 * copies) if deeper immutability is required.
 */
@Value
@Builder(toBuilder = true)
public class Diff {
    Object expectedValue;
    Object actualValue;

    @Override
    public String toString() {
        return "expectedValue=" + expectedValue +
                ", actualValue=" + actualValue;
    }
}
