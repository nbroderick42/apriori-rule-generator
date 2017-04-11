package PTree;

import DataSource.ItemSet;

class PTreeNodeTop extends PTreeNode {

    private ItemSet is;

    PTreeNodeTop(int i) {
        sup = 1;
        is = new ItemSet(i);
    }

    @Override
    protected ItemSet getI() {
        return is;
    }

    @Override
    protected PTreeNode getSibRef() {
        return null;
    }

    @Override
    protected boolean hasSiblings() {
        return false;
    }

    @Override
    protected void setI(ItemSet I) {
        throw new UnsupportedOperationException("Cannot set itemset of PTree.NodeTop");
    }

    @Override
    protected void setSibRef(PTreeNode sibRef) {
        throw new UnsupportedOperationException("Cannot set sibRef of PTree.NodeTop");
    }

    @Override
    public String toString() {
        return String.format("{ sup: %d }", sup);
    }

}