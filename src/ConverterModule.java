/**
 * Created by someone on 11.05.17.
 */
public interface ConverterModule<I,R> {
    R transform(I i);
}
