package TTree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;
import HelperObjects.Rule;

public class TTreeDemo {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Dataset dataset = Dataset.fromFile(Paths.get("data1"), FileFormat.SPACE_SEPARATED);
		
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
		System.out.println(rules);
	}

	private static int convertToIntegerNumerator(double d, int size) {
		// TODO Auto-generated method stub
		return (int) Math.round(d * size);
	}

}
