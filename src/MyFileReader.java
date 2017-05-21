import java.io.*;
import java.util.ArrayList;

/**
 * Created by someone on 12.05.17.
 */
public class MyFileReader {
    public static String[] read(String filePath) throws IOException {
        File f = new File(filePath);
        BufferedReader fin = null;
        fin = new BufferedReader(new FileReader(f));
        String line;
        ArrayList<String> lines = new ArrayList<>();
            while ((line = fin.readLine()) != null) {
                lines.add(line);
            }
            fin.close();
        return lines.toArray(new String[lines.size()]);
    }
}
