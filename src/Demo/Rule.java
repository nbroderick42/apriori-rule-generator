package Demo;

import java.util.Optional;
import java.util.stream.Collectors;

import DataSource.DataSource;
import DataSource.ItemSet;

/**
 * Created by Owner on 4/3/2017.
 */
public class Rule {
    private ItemSet antecedent;
    private double conf;
    private ItemSet consequent;
    private Optional<DataSource> dataset;
    private double sup;

    public Rule(DataSource dataset, ItemSet antecedent, ItemSet consequent, double sup, double conf) {
        this.dataset = Optional.ofNullable(dataset);
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.sup = sup;
        this.conf = conf;
    }

    public Rule(ItemSet antecedent, ItemSet consequent, double sup, double conf) {
        this(null, antecedent, consequent, sup, conf);
    }

    public ItemSet getAntecedent() {
        return antecedent;
    }

    public double getConf() {
        return conf;
    }

    public ItemSet getConsequent() {
        return consequent;
    }

    public double getSup() {
        return sup;
    }

    private String makeItemSetToken(ItemSet is) {
        return is.getItems().stream()
                .map(i -> dataset.map(d -> String.format("%s=%s", d.getHeaderTokenFromValue(i), d.getTableToken(i)))
                        .orElse(i.toString()))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("%s --> %s\n", makeItemSetToken(antecedent), makeItemSetToken(consequent)))
                .append(String.format("\tSupport: %f\n", sup)).append(String.format("\tConfidence: %f\n", conf))
                .toString();
    }

}
