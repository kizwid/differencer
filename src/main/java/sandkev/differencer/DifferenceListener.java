package sandkev.differencer;

import java.io.Serializable;

/**
 * Created by kevsa on 09/12/2017.
 */
public interface DifferenceListener<T extends Serializable> {
    void onMatch(T expected);
    void onDiff(T actual, T expected);
    void onMissing(T expected);
    void onAdded(T actual);
}
