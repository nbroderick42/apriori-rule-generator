package HelperObjects;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


/**
 * Created by Owner on 4/4/2017.
 */
public class FileWriter {
    public static void writeRulesToFileFromList(List<Rule> rules, String filename) throws IOException {
        Path out = Paths.get(filename);
        Files.deleteIfExists(out);      
        List<String> output = rules.stream().map(Rule::toString).collect(toList());
        Files.write(out, output, StandardOpenOption.CREATE_NEW);
    }
}
