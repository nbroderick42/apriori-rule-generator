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
import java.util.function.BiFunction;

import HelperObjects.DataFileHandle;
import HelperObjects.Dataset;
import HelperObjects.Dataset.DataType;
import HelperObjects.Dataset.FileFormat;
import HelperObjects.Rule;
import HelperObjects.RuleGenerator;
import TTree.TTree;

public class Main {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        
        Path toRead = readPath("Please enter the name of the file you wish to read from: ");

        System.out.println("Enter the format of the file: ");
        System.out.println("1. Space-separated");
        System.out.println("2. Comma-separated");
        int fileFormatChoice = selectIntegerInRange("Enter choice: ", 1, 2) - 1;
        FileFormat fileFormat = FileFormat.values()[fileFormatChoice];

        System.out.println("Enter the format of the data: ");
        System.out.println("1. Integer");
        System.out.println("2. String");
        int dataFormatChoice = selectIntegerInRange("Enter choice: ", 1, 2) - 1;
        DataType dataType = DataType.values()[dataFormatChoice];

        System.out.print("Please enter the name of the file you wish to write to: ");
        String toWrite = reader.readLine();

        double minSup = readNormalizedDouble("Please enter the minimum support for frequent itemsets: ");
        double minConf = readNormalizedDouble("Please enter the minimum confidence for rules: ");

        System.out.print("Generating dataset from file... ");
        Dataset dataset = Dataset.build(toRead, fileFormat, dataType);
        System.out.println("done");

        int tableSize = dataset.getTable().size();
        if (tableSize <= 25) {
            System.out.println();
            System.out.println(dataset);
            System.out.println();
        } else {
            System.out.format("Dataset with %d rows read", tableSize);
        }

        int minSupCount = convertToIntegerNumerator(minSup, tableSize);

        List<Rule> rules = generateRules(dataset, minSupCount, minConf);

        writeRulesToFileFromList(rules, toWrite);

        System.out.format("Rule generation complete, written to '%s'\n", toWrite);
    }

    private static int convertToIntegerNumerator(double d, int size) {
        return (int) Math.ceil(d * size);
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

    private static List<Rule> generateRules(Dataset dataset, int minSupport, double minConf) throws IOException {
        System.out.println("Enter the algorithm to run: ");
        System.out.println("1. ARM using Total Support Tree");
        System.out.println("2. ARM using Apriori-TFP");

        int choice = selectIntegerInRange("Enter selection: ", 1, 2);
        BiFunction<Dataset, Integer, RuleGenerator> ruleGenerator;

        switch (choice) {
        case 1:
            ruleGenerator = TTree::fromDataset;
            break;
        case 2:
            ruleGenerator = TTree::fromPTree;
            break;
        default:
            throw new RuntimeException("Unhandled switch case in ruleGenerator");
        }

        System.out.print("Generating rules...");
        long start = System.currentTimeMillis();

        List<Rule> result = ruleGenerator.apply(dataset, minSupport).generateRules(minConf);

        long end = System.currentTimeMillis();
        System.out.println("done");
        System.out.format("Total algorithmic time: %dms\n", end - start);

        return result;
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
