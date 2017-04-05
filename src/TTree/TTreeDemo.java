package TTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;
import HelperObjects.FileWriter;
import HelperObjects.Rule;

public class TTreeDemo {
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter the name of the whitespace-delimited file you wish to read from.");
		String toRead = reader.readLine();
		// TODO Auto-generated method stub
		Dataset dataset = Dataset.fromFile(Paths.get(toRead), FileFormat.SPACE_SEPARATED);
		
		for(List<Integer> row : dataset.getTable()) {
			for(Integer cell : row) {
				System.out.print(cell + "\t");
			}
			System.out.println();
		}

		/**	Temporary values for minSupport and minConfidence = {0.25, 0.4}. In the future, we will be taking in these
		 * 	values from the user.
		 * 	*/
		int tableSize = dataset.getTable().size();
		TTree tree = new TTree(dataset, convertToIntegerNumerator(0.1, tableSize));
		List<Rule> rules = tree.generateRules(0.4);

		System.out.println("Please enter the name of the file you wish to write to.");
		String toWrite = reader.readLine();
		FileWriter.writeRulesToFileFromList(rules, toWrite);
	}

	private static int convertToIntegerNumerator(double d, int size) {
		// TODO Auto-generated method stub
		return (int) Math.ceil(d * size);
	}

}
