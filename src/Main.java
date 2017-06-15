
public class Main {
    public static void main(String[] args) {
        Process process = new Process();
        try {
            process.startTest();
//            process.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
