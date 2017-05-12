/**
 * Created by someone on 12.05.17.
 */
public class SpecialConverter implements ConverterModule<String, Integer> {
    @Override
    public Integer transform(String s) {
        return 666;
    }
}
