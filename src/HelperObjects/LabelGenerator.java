package HelperObjects;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LabelGenerator {
    private final int first;
    private Map<Integer, Map<String, Integer>> tokenToLabelMap;
    private Supplier<Integer> intGenerator;

    LabelGenerator(int start) {
        this(null, start);
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
            return newLabel;
        }
        else {
            return tokenToLabelMap.get(attr).get(token);
        }
    }

    public int getFirstValue() {
        return first;
    }
    
    public int getNumLabels() {
        return tokenToLabelMap.values().size();
    }
    
    private static Supplier<Integer> makeIntGenerator(int start) {
        return new Supplier<Integer>() {
            int idx = start;

            @Override
            public Integer get() {
                return idx++;
            }
        };
    }
}
