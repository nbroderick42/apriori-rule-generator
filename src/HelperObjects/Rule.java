package HelperObjects;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Owner on 4/3/2017.
 */
public class Rule {
    private Optional<Dataset> dataset;
    private ItemSet antecedent;
    private ItemSet consequent;
    private double sup;
    private double conf;

    public Rule(ItemSet antecedent, ItemSet consequent, double sup, double conf) {
        this(null, antecedent, consequent, sup, conf);
    }
    
    public Rule(Dataset dataset, ItemSet antecedent, ItemSet consequent, double sup, double conf) {
        this.dataset = Optional.ofNullable(dataset);
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.sup = sup;
        this.conf = conf;
    }

    public ItemSet getAntecedent() {
        return antecedent;
    }

    public ItemSet getConsequent() {
        return consequent;
    }

    public double getSup() {
        return sup;
    }

    public double getConf() {
        return conf;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("%s --> %s\n", makeItemSetToken(antecedent), makeItemSetToken(consequent)))
                .append(String.format("\tSupport: %f\n", sup))
                .append(String.format("\tConfidence: %f\n", conf))
                .toString();
    }
    
    private String makeItemSetToken(ItemSet is) {
        return is.getItems().stream()
                .map(i -> dataset.map(d -> String.format("%s=%s", d.getHeaderToken(i), d.getTableToken(i))).orElse(i.toString()))
                .collect(Collectors.joining(", ", "[", "]"));       
    }

}
