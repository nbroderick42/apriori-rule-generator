package HelperObjects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import HelperObjects.Dataset.FileFormat;

public class DataFileHandle {

    private Path path;
    private LabelGenerator labelGenerator;
    private FileFormat lineFormatter;
    /* Other metadata to go here */

    private DataFileHandle(Path path, FileFormat fileFormat) throws IOException {
        this.path = path;
        this.lineFormatter = fileFormat;
        this.labelGenerator = new LabelGenerator(1);
    }

    public static DataFileHandle fromPath(Path path, FileFormat fileFormat) throws IOException {
        return new DataFileHandle(path, fileFormat);
    }

    public void forEach(Consumer<ItemSet> action) throws IOException {
        Files.newBufferedReader(path).lines().skip(1).map(lineFormatter.split).map(this::lineToItemSet).forEach(action);
    }

    public int getNumSingletomItemSets() {
        return labelGenerator.getNumLabels();
    }
    
    public LabelGenerator getLabelGenerator() {
        return labelGenerator;
    }
    
    private ItemSet lineToItemSet(String[] line) {
        ItemSet is = new ItemSet();
        for (int i = 0; i < line.length; i++) {
            is.append(labelGenerator.getLabel(i, line[i]));
        }
        return is;
    }
}