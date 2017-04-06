package HelperObjects;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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

    /*
     * A generator of integer labels for the headers of the data
     */
    private LabelGenerator headerLabelGenerator;

    /*
     * A map from each attribute to the possible values that attribute may have.
     */
    private Map<Integer, List<Integer>> valueRangeMap;

    private SortedSet<Integer> valueRangeSet;
    
    private Map<Integer, String> valueToHeaderTokenMap;

    private static final Function<Integer, Predicate<String[]>> hasCorrectNumberOfRows
            = rows -> (tks -> tks.length == rows);
    private static final Predicate<String> isNotEmpty = s -> !s.isEmpty();

    private Dataset(List<List<Integer>> table, Set<Integer> attributeSet, List<List<Integer>> invertedTable,
            LabelGenerator tableLabelGenerator, LabelGenerator headerLabelGenerator,
            Map<Integer, List<Integer>> valueRangeMap, SortedSet<Integer> valueRangeSet) {

        this.table = table;
        this.attributeSet = attributeSet;
        this.invertedTable = invertedTable;
        this.tableLabelGenerator = tableLabelGenerator;
        this.headerLabelGenerator = headerLabelGenerator;
        this.valueRangeMap = valueRangeMap;
        this.valueRangeSet = valueRangeSet;
    }

    private static class LabelGenerator {
        private Map<String, Integer> tokenToLabelMap;
        private Supplier<Integer> labelGenerator;

        private LabelGenerator() {
            this.tokenToLabelMap = new HashMap<>();
            this.labelGenerator = makeIntGenerator(1);
        }

        private LabelGenerator(Map<String, Integer> labels) {
            this.tokenToLabelMap = labels;
            this.labelGenerator = makeIntGenerator(labels.size());
        }

        protected Integer getLabel(String token) {
            if (!tokenToLabelMap.containsKey(token)) {
                tokenToLabelMap.putIfAbsent(token, labelGenerator.get());
            }
            return tokenToLabelMap.get(token);
        }

        private Map<String, Integer> getTokenToLabelMap() {
            return tokenToLabelMap;
        }

        private Map<Integer, String> getLabelToTokenMap() {
            return tokenToLabelMap.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey));
        }

        private Set<Integer> getLabelSet() {
            return new HashSet<>(tokenToLabelMap.values());
        }
    }
    
    /*
     * Returns a dataset generated from a file where all data values are 
     * integers. Throws NumberFormatException if not all data points are integers. 
     */
    private static class IntegerLabelGenerator extends LabelGenerator {
        @Override
        protected Integer getLabel(String token) {
            return Integer.parseInt(token);
        }
    }

    /*
     * Returns a dataset generated from a file without assumed prior labels
     */
    public static Dataset fromFile(Path path, FileFormat format) throws IOException {
        LabelGenerator tableLabelGenerator = new LabelGenerator();
        LabelGenerator headerLabelGenerator = new LabelGenerator();
        return fromFile(path, format, tableLabelGenerator, headerLabelGenerator);
    }
    
    /*
     * Returns a dataset generated from a file without assumed prior labels
     */
    public static Dataset fromIntegerFile(Path path, FileFormat format) throws IOException {
        LabelGenerator tableLabelGenerator = new IntegerLabelGenerator();
        LabelGenerator headerLabelGenerator = new LabelGenerator();
        return fromFile(path, format, tableLabelGenerator, headerLabelGenerator);
    }

    /*
     * Returns a dataset generated from a file assumed labelling. Used for
     * creating test sets built using the labels already generated from a
     * training dataset
     */
    public static Dataset fromFile(Path path, FileFormat format, Map<String, Integer> tableLabels,
            Map<String, Integer> headerLabels) throws IOException {
        LabelGenerator tableLabelGenerator = new LabelGenerator(tableLabels);
        LabelGenerator headerLabelGenerator = new LabelGenerator(headerLabels);
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
        Map<Integer, List<Integer>> valueRangeMap = makeValueRangeMap(invertedTable);
        SortedSet<Integer> valueRangeSet = makeValueRangeSet(valueRangeMap);

        return new Dataset(table, attributeSet, invertedTable, tableLabelGenerator, headerLabelGenerator,
                valueRangeMap, valueRangeSet);
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
        return Arrays.stream(lines).skip(1)
                .map(tokens -> Arrays.stream(tokens).map(labelGenerator::getLabel).collect(toList()))
                .map(Collections::unmodifiableList).collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    /*
     * Given a list of tokens and a label generator, returns the list of
     * attribute labels
     */
    private static Set<Integer> makeAttributeSet(String[][] lines, LabelGenerator headerLabelGenerator) {
        return Arrays.stream(lines[0]).map(headerLabelGenerator::getLabel).collect(toSet());
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
     * Returns a full set of indexes into the table.
     */
    public Set<Integer> getExamples() {
        return IntStream.range(0, table.size()).mapToObj(Integer::valueOf).collect(toSet());
    }

    /*
     * Returns the labels for the table attributes.
     */
    public Set<Integer> getAttributeSet() {
        return attributeSet;
    }

    /*
     * Given a string token, returns the integer label associated with it
     */
    public Integer getTableLabel(String token) {
        return tableLabelGenerator.getLabel(token);
    }

    /*
     * Given an attribute index, returns all possible unique values that
     * attribute may attain
     */
    public List<Integer> getValueRange(Integer attribute) {
        return valueRangeMap.get(attribute);
    }

    /*
     * Returns the entry in the table at the given index
     */
    public List<Integer> getEntry(Integer index) {
        return table.get(index);
    }

    /*
     * Given an attribute index, returns the values in the table for that
     * attribute.
     */
    public List<Integer> getValues(Integer attributeIndex) {
        return invertedTable.get(attributeIndex);
    }

    /*
     * Given a set of indices and an attribute index, return a list of values
     * for that attribute and for all the provided indices
     */
    public List<Integer> getValuesByIndex(Set<Integer> indices, Integer attributeIndex) {
        return indices.stream().map(i -> table.get(i).get(attributeIndex)).collect(toList());
    }

    /*
     * Returns the map of String tokens to integer labels
     */
    public Map<String, Integer> getTableLabels() {
        return tableLabelGenerator.getTokenToLabelMap();
    }

    public Map<String, Integer> getHeaderLabels() {
        return headerLabelGenerator.getTokenToLabelMap();
    }

    /*
     * Given an integer label, returns the String token for that label
     */
    public String getTableToken(Integer label) {
        return tableLabelGenerator.getLabelToTokenMap().get(label);
    }

    /*
     * Given an integer label, returns the String token for that label
     */
    public String getHeaderToken(int label) {
        return headerLabelGenerator.getLabelToTokenMap().get(label);
    }

    /*
     * Returns the table of entries
     */
    public List<List<Integer>> getTable() {
        return table;
    }

    /*
     * Given a set of indices representing a sublist of entries, a target index,
     * and an attribute to split the examples on, returns the resulting entropy
     * of that sublist for that split.
     */
    public double computeEntropy(Set<Integer> indices, Integer targetIndex, Integer attributeIndex) {
        return getValueRange(attributeIndex).stream()
                .map(v -> indices.stream().map(table::get).filter(l -> l.get(attributeIndex).equals(v)))
                .map(s -> s.map(l -> l.get(targetIndex)).collect(toList()))
                .mapToDouble(l -> (double) l.size() * computeEntropy(l) / (double) indices.size()).sum();
    }

    /*
     * For a given list of values, computes the entropy. Note that we do not
     * take a base-2 logarithm as per class instruction, as the logarithm
     * function is monotone increasing irrespective of its base and therefore
     * any base > 1 (including the Math.log base of Euler's constant e) will
     * suffice.
     */
    private static double computeEntropy(List<Integer> values) {
        Collection<Long> counts = values.stream().collect(groupingBy(identity(), counting())).values();
        Double size = counts.stream().mapToDouble(Double::valueOf).sum();
        DoubleUnaryOperator entropyTerm = count -> -count * Math.log(count / (double) size) / (double) size;
        return counts.stream().mapToDouble(Double::valueOf).map(entropyTerm).sum();
    }

    /*
     * Returns all attribute in the dataset which can take on exactly two
     * values.
     */
    public List<String> getAttributeTokens() {
        return attributeSet.stream().map(headerLabelGenerator.getLabelToTokenMap()::get).collect(toList());
    }

    /*
     * For a given target index, returns all attribute labels which are *not*
     * that target.
     */
    public Set<Integer> getNonTargetAttributes(Integer targetIndex) {
        return attributeSet.stream().filter(i -> !i.equals(targetIndex)).collect(toSet());
    }

    public String getTableToken(int attr) {
        return tableLabelGenerator.getLabelToTokenMap().get(attr);
    }
    
    public SortedSet<Integer> getValueRangeSet() {
        return valueRangeSet;
    }

    public int size() {
        return table.size();
    }

    /*
     * Class which abstracts the logic of maintaining and distributing integer
     * labels
     */
    public static class TrainTestDatasetSplit {
        private Dataset trainingSet;
        private Dataset testSet;

        private TrainTestDatasetSplit(Dataset trainingSet, Dataset testSet) {
            this.trainingSet = trainingSet;
            this.testSet = testSet;
        }

        public Dataset getTrainingSet() {
            return trainingSet;
        }

        public Dataset getTestSet() {
            return testSet;
        }
    }

    /*
     * Splits the Dataset into two separate sets. Unfortuantely, this is dead
     * code I did not get to use.
     */
    public TrainTestDatasetSplit splitDataset(double splitRatio) {
        if (splitRatio < 0.0 || splitRatio > 1.0) {
            throw new IllegalArgumentException("Split ration must be between 0.0 and 1.0");
        }
        if (table.size() < 2) {
            throw new IllegalStateException("Cannot split Dataset with fewer than 2 examples");
        }

        Random rand = new Random();
        List<List<Integer>> tableCopy = new ArrayList<List<Integer>>(table);
        Collections.shuffle(tableCopy, rand);

        int splitIndex = (int) (splitRatio * table.size());

        List<List<Integer>> trainingTable = new ArrayList<List<Integer>>(tableCopy.subList(0, splitIndex));
        List<List<Integer>> testTable = new ArrayList<List<Integer>>(tableCopy.subList(splitIndex, tableCopy.size()));

        Dataset trainingDataset = fromTable(trainingTable, attributeSet, tableLabelGenerator, headerLabelGenerator);
        Dataset testDataset = fromTable(testTable, attributeSet, tableLabelGenerator, headerLabelGenerator);

        return new TrainTestDatasetSplit(trainingDataset, testDataset);
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
}