package PTree;

import java.util.function.BiConsumer;

import DataSource.ItemSet;

abstract class PTreeNode {

    @FunctionalInterface
    public static interface LinkFunction extends BiConsumer<PTreeNode, PTreeNode> {
    }

    public static void linkAsChd(PTreeNode src, PTreeNode tgt) {
        src.setChdRef(tgt);
    }

    public static void linkAsSib(PTreeNode src, PTreeNode tgt) {
        src.setSibRef(tgt);
    }

    protected PTreeNode chdRef;

    protected int sup;

    protected PTreeNode getChdRef() {
        return chdRef;
    }

    abstract ItemSet getI();

    abstract PTreeNode getSibRef();

    protected int getSup() {
        return sup;
    }

    protected boolean hasChild() {
        return chdRef != null;
    }

    abstract boolean hasSiblings();

    protected void incSup(int sup) {
        this.sup += sup;
    }

    protected void setChdRef(PTreeNode chdRef) {
        this.chdRef = chdRef;
    };

    abstract void setI(ItemSet I);

    abstract void setSibRef(PTreeNode sibRef);

}