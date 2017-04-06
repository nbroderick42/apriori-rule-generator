package TTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;
import HelperObjects.FileWriter;
import HelperObjects.Rule;

public class TTreeDemo {
    
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
	public static void main(String[] args) throws IOException {
		Path toRead = readPath("Please enter the name of the whitespace-delimited file you wish to read from: ");
		
		Dataset dataset = Dataset.fromFile(toRead, FileFormat.SPACE_SEPARATED);
		int tableSize = dataset.getTable().size();

		System.out.print("Please enter the name of the file you wish to write to: ");
		String toWrite = reader.readLine();
		
		double minSup = readNormalizedDouble("Please enter the minimum support for frequent itemsets: ");
		double minConf = readNormalizedDouble("Please enter the minimum confidence for rules: ");
		
		System.out.println("Generating rules...");
		long start = System.currentTimeMillis();
		TTree tree = new TTree(dataset, convertToIntegerNumerator(minSup, tableSize));
		List<Rule> rules = tree.generateRules(minConf);
		long end = System.currentTimeMillis();

		FileWriter.writeRulesToFileFromList(rules, toWrite);
		
		System.out.format("Rule generation complete, written to '%s'\n", toWrite);
		System.out.format("Total algorithmic running time: %dms\n", end - start);
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

}
