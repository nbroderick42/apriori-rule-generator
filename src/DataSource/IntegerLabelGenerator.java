package DataSource;

public class IntegerLabelGenerator implements LabelGenerator {

    @Override
    public Integer getLabel(Integer attr, String token) {
        return Integer.parseInt(token);
    }

}
