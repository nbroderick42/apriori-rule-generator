package DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class DataFileHandle extends DataSource {

    public static DataSource fromPath(Path path, FileFormat fileFormat) throws IOException {
        return new DataFileHandle(path, fileFormat);
    }

    public DataFileHandle(Path path, FileFormat fileFormat) throws IOException {
        super(path, fileFormat);
        super.preprocessMetadata();
    }

    @Override
    public void forEach(Consumer<ItemSet> action) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.lines().skip(1).map(fileFormat.split).map(this::lineToItemSet).forEach(action);
        }
    }
}