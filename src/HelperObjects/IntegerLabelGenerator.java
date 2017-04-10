package HelperObjects;

class IntegerLabelGenerator extends LabelGenerator {

    IntegerLabelGenerator(int start) {
        super(start);
    }

    @Override
    protected Integer getLabel(Integer attr, String token) {
        return Integer.parseInt(token);
    }
    
}
