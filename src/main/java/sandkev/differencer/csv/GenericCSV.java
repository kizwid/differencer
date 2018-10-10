package sandkev.differencer.csv;

/**
 * Created by kevin on 09/10/2018.
 */
public interface GenericCSV<T> {
    T fromCSV(String csv);
    String toCSV();
}
