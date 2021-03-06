package PTree;

import DataSource.ItemSet;

class PTreeNodeInternal extends PTreeNode {

    private ItemSet I;
    private PTreeNode sibRef;

    PTreeNodeInternal(ItemSet I, int sup) {
        this.I = I;
        this.sup = sup;
    }

    @Override
    public ItemSet getI() {
        return I;
    }

    @Override
    public PTreeNode getSibRef() {
        return sibRef;
    }

    @Override
    boolean hasSiblings() {
        return sibRef != null;
    }

    @Override
    protected void setI(ItemSet I) {
        this.I = I;
    }

    @Override
    protected void setSibRef(PTreeNode sibRef) {
        this.sibRef = sibRef;
    }

    @Override
    public String toString() {
        return String.format("{ sup: %d, I: %s }", sup, I.toString());
    }

}