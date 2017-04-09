package PTree;

import java.io.IOException;
import java.nio.file.Paths;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;
import TTree.TTree;

public class PTreeDemo {

    public static void main(String[] args) throws IOException {
        Dataset dataset = Dataset.fromIntegerFile(Paths.get("data-test"), FileFormat.SPACE_SEPARATED);
        TTree tTree = new TTree(dataset, 0);
        tTree.createFromPTree(1);
    }
}
