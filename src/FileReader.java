import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class FileReader {
    public static List<List<String>> readTableFile(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new java.io.FileReader(new File(filename)));
        String line;
        List<List<String>> table = new ArrayList<>();
        int lineNum = -1;
        while(in.ready()) {
            lineNum++;
            line = in.readLine();
            String[] tokens = line.split("\\s+|\\t+");

            if (lineNum == 0) {
                /** Header line. Add headers to the header list. This will define the columns of the table. */
                for (int i = 0; i < tokens.length; i++) {
                    table.add(new ArrayList<>());
                    table.get(i).add(tokens[i]);
                }
            } else {
                /** Data row. Add values to each column.    */
                for (int i = 0; i < table.size(); i++) {
                    table.get(i).add(tokens[i]);
                }
            }
        }
        in.close();
        return table;
    }
}
