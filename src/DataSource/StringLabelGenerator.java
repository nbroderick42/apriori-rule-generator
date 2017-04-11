package DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StringLabelGenerator implements LabelGenerator {

    protected Supplier<Integer> intGenerator;
    protected Map<Integer, Map<String, Integer>> tokenToLabelMap;

    public StringLabelGenerator(int first) {
        this.intGenerator = makeIntGenerator(first);
        this.tokenToLabelMap = new HashMap<>();
    }

    @Override
    public Integer getLabel(Integer attr, String token) {
        tokenToLabelMap.putIfAbsent(attr, new HashMap<>());

        if (!tokenToLabelMap.get(attr).containsKey(token)) {
            int newLabel = intGenerator.get();
            tokenToLabelMap.get(attr).put(token, newLabel);
            return newLabel;
        } else {
            return tokenToLabelMap.get(attr).get(token);
        }
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
