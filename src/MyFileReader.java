import java.io.*;
import java.util.ArrayList;

/**
 * Created by someone on 12.05.17.
 */
public class MyFileReader {
    public static String[] read(String filePath){
        File f = new File(filePath);
        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        ArrayList<String> lines = new ArrayList<>();
        try {
            while ((line = fin.readLine()) != null) {
                lines.add(line);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);
    }
}
