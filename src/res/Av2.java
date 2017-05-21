package res;

/**
 * Created by someone on 19.03.17.
 */
public class Av2 {

    Integer a1;
    byte [] a2;
    String[] a3;
    boolean a4;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(a1.getClass() + " "+  a1 + "\n");
        sb.append(a2.getClass() + " ");
        for (int i = 0; i < a2.length; i++) {
            sb.append(a2[i] + " ");
        }
        sb.append("\n" + a3.getClass() + " " );
        for(String s: a3){
            sb.append(s + " ");
        }
       return sb.toString();
    }
}
