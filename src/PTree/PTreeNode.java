package PTree;

import java.util.function.BiConsumer;

import DataSource.ItemSet;

abstract class PTreeNode {

    protected int sup;
    protected PTreeNode chdRef;

    protected int getSup() {
        return sup;
    }

    protected PTreeNode getChdRef() {
        return chdRef;
    }

    protected void setChdRef(PTreeNode chdRef) {
        this.chdRef = chdRef;
    }

    protected boolean hasChild() {
        return chdRef != null;
    }
    
    protected void incSup(int sup) {
        this.sup += sup;
    }

    abstract ItemSet getI();

    abstract void setI(ItemSet I);

    abstract PTreeNode getSibRef();

    abstract void setSibRef(PTreeNode sibRef);

    abstract boolean hasSiblings();
    
    @FunctionalInterface
    public static interface LinkFunction extends BiConsumer<PTreeNode, PTreeNode> {};
    
    public static void linkAsChd(PTreeNode src, PTreeNode tgt) {
        src.setChdRef(tgt);
    }
    
    public static void linkAsSib(PTreeNode src, PTreeNode tgt) {
        src.setSibRef(tgt);
    }

}