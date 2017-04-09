package HelperObjects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/*
 * Class used to represent the data with which to create
 * a DecisionTree.
 * 
 * Rather than store the data in its original String format, the
 * entries are all converted into integer format (called "labels") 
 * to facilitate faster processing
 * 
 */

public class Dataset {

    /*
     * The list of data entries in integer format
     */
    private List<List<Integer>> table;

    /*
     * The attributes of the data set
     */
    private Set<Integer> attributeSet;

    /*
     * An inverted table of the data. That is, the rows of this table are the
     * attributes and each rows contains all the values for the given attribute.
     */
    private List<List<Integer>> invertedTable;

    /*
     * A genereator of integer labels for the table of data
     */
    private LabelGenerator tableLabelGenerator;

    private SortedSet<Integer> valueRangeSet;

    private Map<Integer, String> valueToHeaderTokenMap;

    private static final Function<Integer, Predicate<String[]>> hasCorrectNumberOfRows
            = rows -> (tks -> tks.length == rows);
    private static final Predicate<String> isNotEmpty = s -> !s.isEmpty();

    private Dataset(List<List<Integer>> table, Set<Integer> attributeSet, List<List<Integer>> invertedTable,
            LabelGenerator tableLabelGenerator, LabelGenerator headerLabelGenerator,
            Map<Integer, List<Integer>> valueRangeMap, SortedSet<Integer> valueRangeSet,
            Map<Integer, String> valueToHeaderTokenMap) {

        this.table = table;
        this.attributeSet = attributeSet;
        this.invertedTable = invertedTable;
        this.tableLabelGenerator = tableLabelGenerator;
        this.valueRangeSet = valueRangeSet;
        this.valueToHeaderTokenMap = valueToHeaderTokenMap;
    }

    private static class LabelGenerator {
        private final int first;
        private Map<Integer, Map<String, Integer>> tokenToLabelMap;
        private Supplier<Integer> intGenerator;

        protected LabelGenerator(int start) {
            this(null, start);
        }

        protected LabelGenerator() {
            this(null, 0);
        }

        public LabelGenerator(Map<Integer, Map<String, Integer>> labels, int first) {
            this.tokenToLabelMap = labels != null ? labels : new HashMap<>();
            this.intGenerator = makeIntGenerator(first);
            this.first = first;
        }

        protected Integer getLabel(Integer attr, String token) {
            tokenToLabelMap.putIfAbsent(attr, new HashMap<>());

            if (!tokenToLabelMap.get(attr).containsKey(token)) {
                int newLabel = intGenerator.get();
                tokenToLabelMap.get(attr).put(token, newLabel);
            }

            return tokenToLabelMap.get(attr).get(token);
        }

        private Map<Integer, String> getLabelToTokenMap() {
            return tokenToLabelMap.entrySet().stream().map(e -> e.getValue()).flatMap(
                    m -> m.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey)).entrySet().stream())
                    .collect(toMap(Entry::getKey, Entry::getValue));
        }

        private int getFirstValue() {
            return first;
        }

        public Set<Integer> getLabelSet() {
            return tokenToLabelMap.entrySet().stream().flatMap(e -> e.getValue().values().stream()).collect(toSet());
        }
    }

    /*
     * Returns a dataset generated from a file where all data values are
     * integers. Throws NumberFormatException if not all data points are
     * integers.
     */
    private static class IntegerLabelGenerator extends LabelGenerator {

        @Override
        protected Integer getLabel(Integer attr, String token) {
            return Integer.parseInt(token);
        }
    }

    public static Dataset build(Path path, FileFormat format, DataType type) throws IOException {
        return type.build(path, format);
    }
    
    /*
     * Returns a dataset generated from a file without assumed prior labels
     */
    private static Dataset fromFile(Path path, FileFormat format) throws IOException {
        LabelGenerator tableLabelGenerator = new LabelGenerator(1);
        LabelGenerator headerLabelGenerator = new LabelGenerator(1);
        return fromFile(path, format, tableLabelGenerator, headerLabelGenerator);
    }

    /*
     * Returns a dataset generated from a file without assumed prior labels
     */
    private static Dataset fromIntegerFile(Path path, FileFormat format) throws IOException {
        LabelGenerator tableLabelGenerator = new IntegerLabelGenerator();
        LabelGenerator headerLabelGenerator = new LabelGenerator(1);
        return fromFile(path, format, tableLabelGenerator, headerLabelGenerator);
    }

    /*
     * Private helper methods to generate the Dataset
     */
    private static Dataset fromFile(Path path, FileFormat format, LabelGenerator tableLabelGenerator,
            LabelGenerator headerLabelGenerator) throws IOException {
        String[][] lines = readLines(path, format);
        return fromLines(lines, tableLabelGenerator, headerLabelGenerator);
    }

    private static Dataset fromLines(String[][] lines, LabelGenerator tableLabelGenerator,
            LabelGenerator headerLabelGenerator) {
        List<List<Integer>> table = makeTable(lines, tableLabelGenerator);
        Set<Integer> attributes = makeAttributeSet(lines, headerLabelGenerator);

        return fromTable(table, attributes, tableLabelGenerator, headerLabelGenerator);
    }

    private static Dataset fromTable(List<List<Integer>> table, Set<Integer> attributes,
            LabelGenerator tableLabelGenerator, LabelGenerator headerLabelGenerator) {

        Set<Integer> attributeSet = headerLabelGenerator.getLabelSet();
        List<List<Integer>> invertedTable = makeInvertedTable(table);
        Map<Integer, String> valueToHeaderTokenMap = makeValueToHeaderTokenMap(invertedTable, headerLabelGenerator);
        Map<Integer, List<Integer>> valueRangeMap = makeValueRangeMap(invertedTable);
        SortedSet<Integer> valueRangeSet = makeValueRangeSet(valueRangeMap);

        return new Dataset(table, attributeSet, invertedTable, tableLabelGenerator, headerLabelGenerator, valueRangeMap,
                valueRangeSet, valueToHeaderTokenMap);
    }

    private static SortedSet<Integer> makeValueRangeSet(Map<Integer, List<Integer>> valueRangeMap) {
        return valueRangeMap.values().stream().reduce(new ArrayList<>(), (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        }).stream().collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
    }

    /*
     * Given a list of tokens and a label generator, produce the table of data
     */
    private static List<List<Integer>> makeTable(String[][] lines, LabelGenerator labelGenerator) {
        List<List<Integer>> table = new ArrayList<>();
        Iterator<String[]> it = Arrays.asList(lines).listIterator(1);

        if (!it.hasNext()) {
            return table;
        }

        it.forEachRemaining(line -> {
            List<Integer> row = new ArrayList<>();
            for (int i = 0; i < line.length; i++) {
                row.add(labelGenerator.getLabel(i, line[i]));
            }
            table.add(row);
        });

        return table;
    }

    /*
     * Given a list of tokens and a label generator, returns the list of
     * attribute labels
     */
    private static Set<Integer> makeAttributeSet(String[][] lines, LabelGenerator headerLabelGenerator) {
        Set<Integer> attributes = new HashSet<>();
        String[] header = lines[0];

        for (int i = 0; i < header.length; i++) {
            attributes.add(headerLabelGenerator.getLabel(i, header[i]));
        }

        return attributes;
    }

    /*
     * Given a table of entries, return an inverted copy.
     */
    private static List<List<Integer>> makeInvertedTable(List<List<Integer>> table) {
        List<List<Integer>> invertedTable = new ArrayList<>();
        for (int i = 0; i < table.get(0).size(); i++) {
            invertedTable.add(new ArrayList<>());
            for (int j = 0; j < table.size(); j++) {
                invertedTable.get(i).add(table.get(j).get(i));
            }
        }
        return invertedTable;
    }

    /*
     * Create a map of all unique values each attribute can attain.
     */
    private static Map<Integer, List<Integer>> makeValueRangeMap(List<List<Integer>> invertedTable) {
        Supplier<Integer> intGenerator = makeIntGenerator();
        Function<List<Integer>, Integer> generateIndex = s -> intGenerator.get();
        return invertedTable.stream().map(list -> list.stream().distinct().collect(toList()))
                .map(Collections::unmodifiableList).collect(toMap(generateIndex, Function.identity()));
    }

    private static Map<Integer, String> makeValueToHeaderTokenMap(List<List<Integer>> invertedTable,
            LabelGenerator headerLabelGenerator) {

        Map<Integer, String> valueToHeaderTokenMap = new HashMap<>();
        Map<Integer, String> headerLabelToTokenMap = headerLabelGenerator.getLabelToTokenMap();

        for (int i = 0; i < invertedTable.size(); i++) {
            final int idx = i + headerLabelGenerator.getFirstValue();
            invertedTable.get(i).stream().distinct()
                    .forEach(item -> valueToHeaderTokenMap.put(item, headerLabelToTokenMap.get(idx)));
        }
        return valueToHeaderTokenMap;
    }

    /*
     * Read all non-empty lines from a file and return a list of tokens for that
     * file.
     */
    private static String[][] readLines(Path path, FileFormat format) throws IOException {
        String[][] lines = Files.readAllLines(path).stream().filter(isNotEmpty).map(String::toLowerCase)
                .map(format.split).toArray(String[][]::new);

        int cols = lines[0].length;

        return Arrays.stream(lines).filter(hasCorrectNumberOfRows.apply(cols)).toArray(String[][]::new);
    }

    /*
     * Returns an sequential integer generator. Essentially, this is just how
     * the Stream API forces us to increment an index.
     */
    private static Supplier<Integer> makeIntGenerator(int start) {
        return new Supplier<Integer>() {
            int idx = start;

            @Override
            public Integer get() {
                return idx++;
            }
        };
    }

    private static Supplier<Integer> makeIntGenerator() {
        return makeIntGenerator(0);
    }

    /*
     * Given an attribute index, returns the values in the table for that
     * attribute.
     */
    public List<Integer> getValues(Integer attributeIndex) {
        return invertedTable.get(attributeIndex);
    }

    public String getTableToken(Integer label) {
        return tableLabelGenerator.getLabelToTokenMap().get(label);
    }

    public String getHeaderTokenFromValue(int i) {
        return valueToHeaderTokenMap.get(i);
    }

    /*
     * Returns the table of entries
     */
    public List<List<Integer>> getTable() {
        return table;
    }

    /*
     * For a given target index, returns all attribute labels which are *not*
     * that target.
     */
    public Set<Integer> getNonTargetAttributes(Integer targetIndex) {
        return attributeSet.stream().filter(i -> !i.equals(targetIndex)).collect(toSet());
    }

    public SortedSet<Integer> getValueRangeSet() {
        return valueRangeSet;
    }

    /*
     * Enum type defining different file formats and the mapping functions used
     * to tokenize them. Unfortunately, this is superfluous now, as I did not
     * test this on any other data files other than the sets from the assignment
     */
    public enum FileFormat {
        SPACE_SEPARATED(s -> s.split("\\s+")), COMMA_SEPARATED(s -> s.split(","));

        public Function<String, String[]> split;

        private FileFormat(Function<String, String[]> split) {
            this.split = split;
        }
    }

    @Override
    public String toString() {
        return table.stream().map(row -> row.stream().sorted(Integer::compareTo).collect(toList()))
                .sorted(ItemSet::compare).map(l -> l.stream().map(Object::toString).collect(joining("\t")))
                .collect(joining("\n"));
    }
    
    public enum DataType implements DatasetBuilder {
        INTEGER {
            @Override
            public Dataset build(Path path, FileFormat fileFormat) throws IOException {
                return fromIntegerFile(path, fileFormat);
            }
        }, 
        STRING {
            @Override
            public Dataset build(Path path, FileFormat fileFormat) throws IOException {
                return fromFile(path, fileFormat);
            }
        };
    }
    
    @FunctionalInterface
    public interface DatasetBuilder {
        public Dataset build(Path path, FileFormat fileFormat) throws IOException;
    }

}