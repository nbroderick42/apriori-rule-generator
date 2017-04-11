package DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class DataSource {

    protected Path path;
    protected FileFormat fileFormat;

    private LabelGenerator labelGenerator;
    private boolean firstPass;

    private Set<Integer> uniqueItems;
    private Map<Integer, String> tokenMap;
    private Map<Integer, String> headerMap;

    private int numUniqueItems;
    private int numRecords;
    private List<String> headers;

    protected DataSource(Path path, FileFormat fileFormat) throws IOException {
        this.path = path;
        this.fileFormat = fileFormat;
        this.labelGenerator = new LabelGenerator(1);
        this.firstPass = true;

        this.uniqueItems = new HashSet<>();
        this.tokenMap = new HashMap<>();
        this.headerMap = new HashMap<>();
    }

    protected void preprocessMetadata(Consumer<ItemSet> forEachItemSet) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            headers = Stream.of(br.readLine()).map(fileFormat.split).map(Arrays::asList).findFirst().get();
            br.lines().map(fileFormat.split).map(this::lineToItemSet)
                    .forEach(forEachItemSet.andThen(is -> numRecords++));
            numUniqueItems = uniqueItems.size();
            uniqueItems = null;
            firstPass = false;
        }
    }

    public abstract void forEach(Consumer<ItemSet> action) throws IOException;

    public int getNumUniqueItems() {
        return numUniqueItems;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public LabelGenerator getLabelGenerator() {
        return labelGenerator;
    }

    protected ItemSet lineToItemSet(String[] line) {
        ItemSet is = new ItemSet();

        for (int i = 0; i < line.length; i++) {
            int label = labelGenerator.getLabel(i, line[i]);
            is.append(label);
            if (firstPass) {
                uniqueItems.add(label);
                tokenMap.putIfAbsent(label, line[i]);
                headerMap.putIfAbsent(label, headers.get(i));
            }
        }

        return is;
    }

    public String getHeaderTokenFromValue(Integer i) {
        return headerMap.get(i);
    }

    public String getTableToken(Integer i) {
        return tokenMap.get(i);
    }

    public void preprocessMetadata() throws IOException {
        preprocessMetadata(is -> {
        });
    }

    public static DataSource fromPath(Path path, FileFormat format, Class<? extends DataSource> type)
            throws IOException {
        if (type.equals(DataSet.class)) {
            return DataSet.fromPath(path, format);
        } else {
            return fromPath(path, format);
        }
    }

    public static DataSource fromPath(Path path, FileFormat format) throws IOException {
        return DataFileHandle.fromPath(path, format);
    }
}
