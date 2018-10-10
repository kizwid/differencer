package sandkev.differencer;

import java.io.Serializable;

/**
 * To be implemented by any object that can be uniquely identified by its natural key
 */
public interface NaturallyKeyed<K extends Serializable> extends Serializable {
    K getNaturalKey();
}