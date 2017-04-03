package PTree;

import java.io.IOException;
import java.nio.file.Paths;

import HelperObjects.Dataset;
import HelperObjects.Dataset.FileFormat;

public class PTreeDemo {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        Dataset dataset = Dataset.fromIntegerFile(Paths.get("data-test"), FileFormat.SPACE_SEPARATED);

        @SuppressWarnings("unused")
        PTree tree = new PTree(dataset);
    }

}
