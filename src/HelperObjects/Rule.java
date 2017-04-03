package HelperObjects;

/**
 * Created by Owner on 4/3/2017.
 */
public class Rule {
    private ItemSet antecedent;
    private ItemSet consequent;
    private double sup;
    private double conf;

    public Rule(ItemSet antecedent, ItemSet consequent, double sup, double conf) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.sup = sup;
        this.conf = conf;
    }

    @Override
    public String toString() {
        return antecedent + " -> " + consequent;
    }

}
