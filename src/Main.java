import java.util.ArrayList;

/**
 * Created by someone on 19.03.17.
 */
public class Main {
    public static void main(String[] args) {
        ArrayList<String> als = new ArrayList<>();
        als.add("one");
        als.add("two");
        als.add("three");
        Av1 av1 = new Av1();
        av1.a1 = 4;
        av1.a2 = "fore";
        av1.a3 = als;
        System.out.println(Converter.convert(av1).toString());
    }
}
