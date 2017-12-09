package sandkev.differencer;

import java.io.Serializable;

/**
 * To be implemented by any object that can be uniquely identified by its natural key
 */
public interface NaturallyKeyed<N extends Serializable> extends Serializable {
    N getKey();
}