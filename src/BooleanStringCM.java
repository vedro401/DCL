/**
 * Created by someone on 20.05.17.
 */
public class BooleanStringCM implements ConverterModule<Boolean,String> {
    @Override
    public String transform(Boolean aBoolean) {
        return aBoolean ? "Yep" : "Nop";
    }
}
