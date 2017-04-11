package DataSource;

public class IntegerLabelGenerator extends LabelGenerator {

    public IntegerLabelGenerator(int start) {
        super(start);
    }

    @Override
    protected Integer getLabel(Integer attr, String token) {
        return Integer.parseInt(token);
    }

}
