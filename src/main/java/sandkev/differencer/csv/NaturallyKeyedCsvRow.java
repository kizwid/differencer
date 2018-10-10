package sandkev.differencer.csv;

import sandkev.differencer.NaturallyKeyed;

/**
 * Created by kevin on 08/10/2018.
 */
public class NaturallyKeyedCsvRow<K>  {
    String[] fieldNames;
    String[] dataTypeNames;
    String[] data;
    int[] naturalKeyFields;
    int[] ignoredFields;
    K naturalKey;
}
