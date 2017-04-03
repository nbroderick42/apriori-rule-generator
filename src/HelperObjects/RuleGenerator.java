package HelperObjects;

import java.util.List;

/**
 * Created by Owner on 4/3/2017.
 */
public interface RuleGenerator {
    List<Rule> generateRules(double confidence);
}
