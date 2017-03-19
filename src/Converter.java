/**
 * Created by someone on 19.03.17.
 */
public class Converter {
    public static Av2 convert(Av1 av1){
        Av2 av2 = new Av2();
        av2.a1 = av1.a1;
        byte b[] = new byte[av1.a2.length()];
        for (int i = 0; i < av1.a2.length(); i++) {
            b[i] = (byte)av1.a2.charAt(i);
        }

        av2.a2 = b;
        av2.a3 = new String[av1.a3.size()];
        av1.a3.toArray(av2.a3);
        return av2;
    }
}
