import java.util.ArrayList;

/**
 * Created by someone on 19.03.17.
 */
public class Main {
    public static void main(String[] args) {
        Process process = new Process();
        try {
            process.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
