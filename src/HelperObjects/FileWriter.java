package HelperObjects;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Owner on 4/4/2017.
 */
public class FileWriter {
    public static void writeRulesToFileFromList(List<Rule> rules, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filename));

        StringBuilder sb;
        for(Rule r : rules) {
            sb = new StringBuilder();
            sb.append(r.getAntecedent()).append(" --> ");
            sb.append(r.getConsequent()).append("\n");
            sb.append("\t");
            sb.append("Support: ").append(r.getSup()).append("\n\t");
            sb.append("Confidence: ").append(r.getConf()).append("\n");
            writer.write(sb.toString());
        }
        writer.close();
    }
}
