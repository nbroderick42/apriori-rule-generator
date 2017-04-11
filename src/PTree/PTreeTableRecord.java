package PTree;

import DataSource.ItemSet;

public class PTreeTableRecord {
    private ItemSet label;
    private ItemSet ancestors;
    private int sup;

    PTreeTableRecord(ItemSet label, ItemSet ancestors, int sup) {
        this.label = label;
        this.ancestors = ancestors;
        this.sup = sup;
    }

    public ItemSet getLabel() {
        return label;
    }

    public ItemSet getAncestors() {
        return ancestors;
    }

    public int getSup() {
        return sup;
    }
}