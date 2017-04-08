package PTree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;

public class PTreeDemo {

    public static void main(String[] args) throws IOException {
        Dataset dataset = Dataset.fromFile(Paths.get("ChessData.txt"), FileFormat.SPACE_SEPARATED);

        for(List<Integer> row : dataset.getTable()) {
            for(Integer cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
        
        PTree tree = new PTree(dataset);
        System.out.println(tree);
    }
}
