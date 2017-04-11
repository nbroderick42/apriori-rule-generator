package Demo;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import DataSource.DataSet;
import DataSource.DataSource;
import DataSource.FileFormat;
import TTree.TTree;

public class Main {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static List<Rule> generateRules(DataSource dataset, double minSup, double minConf) throws IOException {
        System.out.println("Enter the algorithm to run: ");
        System.out.println("1. ARM using Total Support Tree");
        System.out.println("2. ARM using Apriori-TFP");

        long start, end;
        List<Rule> result;
        int choice = selectIntegerInRange("Enter selection: ", 1, 2);

        System.out.print("Generating rules...");

        switch (choice) {
        case 1:
            start = System.currentTimeMillis();
            result = TTree.fromDataset(dataset, minSup).generateRules(minConf);
            end = System.currentTimeMillis();
            break;
        case 2:
            start = System.currentTimeMillis();
            result = TTree.fromPTree(dataset, minSup).generateRules(minConf);
            end = System.currentTimeMillis();
            break;
        default:
            throw new RuntimeException("Unhandled switch case in ruleGenerator");
        }

        System.out.println("done");
        System.out.format("Total algorithmic time: %dms\n", end - start);

        return result;
    }

    public static void main(String[] args) throws IOException {

        Path toRead = readPath("Please enter the name of the file you wish to read from: ");

        System.out.println("Enter the format of the file: ");
        System.out.println("1. Space-separated");
        System.out.println("2. Comma-separated");
        int fileFormatChoice = selectIntegerInRange("Enter choice: ", 1, 2) - 1;
        FileFormat fileFormat = FileFormat.values()[fileFormatChoice];

        System.out.print("Please enter the name of the file you wish to write to: ");
        String toWrite = reader.readLine();

        double minSup = readNormalizedDouble("Please enter the minimum support for frequent itemsets: ");
        double minConf = readNormalizedDouble("Please enter the minimum confidence for rules: ");

        DataSource dataSource = DataSource.fromPath(toRead, fileFormat, DataSet.class);
        List<Rule> rules = generateRules(dataSource, minSup, minConf);

        writeRulesToFileFromList(rules, toWrite);
    }

    private static double readNormalizedDouble(String prompt) throws IOException {
        while (true) {
            try {
                System.out.print(prompt);
                double result = Double.parseDouble(reader.readLine());
                if (result < 0.0 || result > 1.0) {
                    System.out.println("Number must be between 0.0 and 1.0");
                } else {
                    return result;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("This number is not in valid format");
            }
        }

    }

    private static Path readPath(String prompt) throws IOException {
        while (true) {
            try {
                System.out.print(prompt);
                Path path = Paths.get(reader.readLine());
                if (path.toFile().exists()) {
                    return path;
                } else {
                    System.out.format("Cannot open file %s: no such file\n", path);
                }
            } catch (InvalidPathException e) {
                System.out.println("This path cannot be parsed");
            }
        }
    }

    private static int selectIntegerInRange(String prompt, int begin, int end) throws IOException {
        while (true) {
            try {
                System.out.print(prompt);
                int result = Integer.parseInt(reader.readLine());
                if (result < begin || result > end) {
                    System.out.println("Selection not valid");
                } else {
                    return result;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Selection not valid");
            }
        }
    }

    public static void writeRulesToFileFromList(List<Rule> rules, String filename) throws IOException {
        Path out = Paths.get(filename);
        Files.deleteIfExists(out);
        List<String> output = rules.stream().map(Rule::toString).collect(toList());
        Files.write(out, output, StandardOpenOption.CREATE_NEW);
    }

}
