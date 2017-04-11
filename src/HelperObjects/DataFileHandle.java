package HelperObjects;

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

import HelperObjects.Dataset.FileFormat;

public class DataFileHandle {

    private Path path;
    private LabelGenerator labelGenerator;
    private FileFormat lineFormatter;
    private boolean firstPass;

    private Set<Integer> uniqueItems;
    private Map<Integer, String> tokenMap;
    private Map<Integer, String> headerMap;
    
    private int numUniqueItems;
    private int numRecords;
    private List<String> headers;
    
    public DataFileHandle(Path path, FileFormat fileFormat) throws IOException {        
        this.path = path;        
        this.lineFormatter = fileFormat;
        this.labelGenerator = new LabelGenerator(1);
        this.firstPass = true;
        
        this.uniqueItems = new HashSet<>();
        this.tokenMap = new HashMap<>();
        this.headerMap = new HashMap<>();
        
        preprocessMetadata();
    }

    public static DataFileHandle fromPath(Path path, FileFormat fileFormat) throws IOException {
        return new DataFileHandle(path, fileFormat);
    }
    
    public void preprocessMetadata() throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            headers = Stream.of(br.readLine()).map(lineFormatter.split).map(Arrays::asList).findFirst().get();
            br.lines().map(lineFormatter.split).map(this::lineToItemSet).forEach(is -> numRecords++);
            numUniqueItems = uniqueItems.size();
            uniqueItems = null;
            firstPass = false;
        }
    }

    public void forEach(Consumer<ItemSet> action) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.lines().skip(1).map(lineFormatter.split).map(this::lineToItemSet).forEach(action);
        }
    }

    public int getNumUniqueItems() {
        return numUniqueItems;
    }
    
    public int getNumRecords() {
        return numRecords;
    }
    
    public LabelGenerator getLabelGenerator() {
        return labelGenerator;
    }
    
    private ItemSet lineToItemSet(String[] line) {
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
}