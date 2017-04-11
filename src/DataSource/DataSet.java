package DataSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataSet extends DataSource {

    public static DataSource fromPath(Path path, FileFormat fileFormat) throws IOException {
        return new DataSet(path, fileFormat);
    }

    private List<ItemSet> itemSets;

    public DataSet(Path path, FileFormat fileFormat) throws IOException {
        super(path, fileFormat);
        itemSets = new ArrayList<>();
        preprocessMetadata(itemSets::add);
    }

    @Override
    public void forEach(Consumer<ItemSet> action) throws IOException {
        itemSets.forEach(action);
    }
}