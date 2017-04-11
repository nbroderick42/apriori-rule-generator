package PTree;

import DataSource.ItemSet;

public class PTreeTableRecord {
    private ItemSet ancestors;
    private ItemSet label;
    private int sup;

    PTreeTableRecord(ItemSet label, ItemSet ancestors, int sup) {
        this.label = label;
        this.ancestors = ancestors;
        this.sup = sup;
    }

    public ItemSet getAncestors() {
        return ancestors;
    }

    public ItemSet getLabel() {
        return label;
    }

    public int getSup() {
        return sup;
    }
}