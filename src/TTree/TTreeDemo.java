package TTree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;

public class TTreeDemo {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Dataset dataset = Dataset.fromIntegerFile(Paths.get("data-test"), FileFormat.SPACE_SEPARATED);
		
		for(List<Integer> row : dataset.getTable()) {
			for(Integer cell : row) {
				System.out.print(cell + "\t");
			}
			System.out.println();
		}
		
		TTree tree = new TTree(dataset, convertToIntegerNumerator(0.35, dataset.getTable().size()));
	}

	private static int convertToIntegerNumerator(double d, int size) {
		// TODO Auto-generated method stub
		return (int) Math.round(d * size);
	}

}
