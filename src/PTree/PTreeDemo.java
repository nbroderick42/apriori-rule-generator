package PTree;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;

public class PTreeDemo {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        Dataset dataset = Dataset.fromIntegerFile(Paths.get("data-test"), FileFormat.SPACE_SEPARATED);

        for(List<Integer> row : dataset.getTable()) {
            for(Integer cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

        @SuppressWarnings("unused")
        PTree tree = new PTree(dataset);
        System.out.println(tree);
    }
}
