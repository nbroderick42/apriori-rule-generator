package PTree;

import java.io.IOException;
import java.nio.file.Paths;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;

public class PTreeDemo {

    public static void main(String[] args) throws IOException {
        Dataset dataset = Dataset.fromFile(Paths.get("ChessData.txt"), FileFormat.SPACE_SEPARATED);
        System.out.println(dataset);
        PTree tree = new PTree(dataset);
        System.out.println(tree.getLargestItemSetSize());
    }
}
