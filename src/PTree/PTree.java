package PTree;

import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.ItemSet;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class PTree {

    private Dataset dataset;
    private NodeTop[] start;
    private int[] nodeCardinalityCounts;
    private PTreeTable pTreeTable;

    private enum LinkFlag {
        CHILD {
            @Override
            public void link(Node src, Node tgt) {
                src.setChdRef(tgt);
            }
        },
        SIBLING {
            @Override
            public void link(Node src, Node tgt) {
                src.setSibRef(tgt);
            }
        };
        public abstract void link(Node src, Node tgt);

    }

    public PTree(Dataset dataset) {
        this.dataset = dataset;
        this.start = new NodeTop[dataset.getValueRangeSet().size() + 1];
        this.nodeCardinalityCounts = new int[dataset.getValueRangeSet().size() + 1];

        createPtree();
        
        this.pTreeTable = new PTreeTable(this);
    }
    private void createPtree() {
        List<List<Integer>> R = dataset.getTable();
        R.forEach(this::addToPtreeTopLevel);
    }

    private void addToPtreeTopLevel(List<Integer> r) {
        ItemSet is = new ItemSet(r);
        
        if (is.isEmpty()) {
            return;
        }
        
        int r0 = is.get(0);
        if (start[r0] == null) {
            start[r0] = new NodeTop(r0);
            nodeCardinalityCounts[1]++;
        } else {
            start[r0].sup++;
        }

        if (r.size() > 1) {
            addToPtree(LinkFlag.CHILD, start[r0].chdRef, ItemSet.del1(is), start[r0], 2, r.size());
        }
    }
    
    private NodeInternal createPTreeInternalNode(ItemSet I, int level) {
        return createPTreeInternalNode(I, 0, level);
    }
    
    private NodeInternal createPTreeInternalNode(ItemSet I, int sup, int level) {
        nodeCardinalityCounts[level]++;
        return new NodeInternal(I, sup);
    }

    private void addToPtree(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        if (ref == null) {
            Node newRef = createPTreeInternalNode(r, 1, itemSetLength);
            f.link(oldRef, newRef);
        } else {
            if (ref.getI().equals(r)) {
                ref.sup++;
                return;
            }

            boolean rLessThanRef = ref.getI().greaterThan(r);
            boolean rContainedInRef = ref.getI().contains(r);
            boolean rContainsRef = ref.getI().containedBy(r);

            if (rLessThanRef && rContainedInRef) {
                parent(f, ref, r, oldRef, parentLength, itemSetLength);
            } else if (rLessThanRef) {
                eldSib(f, ref, r, oldRef, parentLength, itemSetLength);
            } else if (rContainsRef) {
                child(ref, r, parentLength, itemSetLength);
            } else {
                yngSib(f, ref, r, oldRef, parentLength, itemSetLength);
            }
        }
    }

    private void parent(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        NodeInternal newRef = createPTreeInternalNode(r, ref.sup + 1, itemSetLength);
        newRef.chdRef = ref;
        f.link(oldRef, newRef);
        ref.setI(ItemSet.delN(ref.getI(), r));
        moveSiblings(ref, newRef);
    }

    private void child(Node ref, ItemSet r, int parentLength, int itemSetLength) {
        ref.incSup();
        if (!ref.hasChild()) {
            NodeInternal newRef = createPTreeInternalNode(ItemSet.delN(r, ref.getI()), 1, itemSetLength);
            ref.setChdRef(newRef);
        } else {
            addToPtree(LinkFlag.CHILD, ref.getChdRef(), ItemSet.delN(r, ref.getI()), ref, parentLength + ref.getI().size(), itemSetLength);
        }
    }

    private void eldSib(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1); 
            f.link(oldRef, newPref);
            r = ItemSet.delN(r, lss);
            newPref.setChdRef(createPTreeInternalNode(r, 1, itemSetLength));
            newPref.getChdRef().setSibRef(ref);
            ref.setI(ItemSet.delN(ref.getI(), lss));
            moveSiblings(ref, newPref);
        } else {
            NodeInternal newSref = createPTreeInternalNode(r, itemSetLength);
            newSref.setSibRef(ref);
            f.link(oldRef, newSref);
        }
    }

    private void yngSib(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        if (!ref.hasSiblings()) {
            yngSib1(f, ref, r, oldRef, parentLength, itemSetLength);
        } else {
            yngSib2(f, ref, r, oldRef, parentLength, itemSetLength);
        }
    }

    private void yngSib1(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1);
            f.link(oldRef, newPref);
            ref.setI(ItemSet.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            ref.setSibRef(createPTreeInternalNode(ItemSet.delN(r, lss), 1, itemSetLength));
        } else {
            ref.setSibRef(createPTreeInternalNode(r, 1, itemSetLength));
        }
    }

    private void yngSib2(LinkFlag f, Node ref, ItemSet r, Node oldRef, int parentLength, int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1);
            f.link(oldRef, newPref);
            ref.setI(ItemSet.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            Node tempRef = ref.getSibRef();
            ref.setSibRef(createPTreeInternalNode(r, 1, itemSetLength));
            ref.getSibRef().setSibRef(tempRef);
            moveSiblings(ref, newPref);
        } else {
            addToPtree(LinkFlag.SIBLING, ref.getSibRef(), r, ref, parentLength + ref.getI().size(), itemSetLength);
        }
    }

    private static void moveSiblings(Node from, Node to) {
        if (from.hasSiblings()) {
            to.setSibRef(from.getSibRef());
            from.setSibRef(null);
        }
    }

    static abstract class Node {

        protected int sup;
        protected Node chdRef;
        
        protected int getSup() {
            return sup;
        }

        protected Node getChdRef() {
            return chdRef;
        }

        protected void setChdRef(Node chdRef) {
            this.chdRef = chdRef;
        }

        protected void incSup() {
            sup++;
        }

        protected boolean hasChild() {
            return chdRef != null;
        }

        abstract ItemSet getI();

        abstract void setI(ItemSet I);

        abstract Node getSibRef();

        abstract void setSibRef(Node sibRef);

        abstract boolean hasSiblings();

    }
    
    static class NodeTop extends Node {

        private ItemSet is;
        
        private NodeTop(int i) {
            sup = 1;
            is = new ItemSet(i);
        }

        @Override
        protected ItemSet getI() {
            return is;
        }

        @Override
        protected Node getSibRef() {
            return null;
        }

        @Override
        protected void setSibRef(Node sibRef) {
            throw new UnsupportedOperationException("Cannot set sibRef of PTree.NodeTop");
        }

        @Override
        protected void setI(ItemSet I) {
            throw new UnsupportedOperationException("Cannot set itemset of PTree.NodeTop");
        }

        @Override
        protected boolean hasSiblings() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("{ sup: %d }", sup);
        }

    }

    static class NodeInternal extends Node {

        private ItemSet I;
        private Node sibRef;
        private NodeInternal(ItemSet I, int sup) {
            this.I = I;
            this.sup = sup;
            
        }

        private NodeInternal(ItemSet I) {
            this.I = I;
        }

        @Override
        public ItemSet getI() {
            return I;
        }

        @Override
        public Node getSibRef() {
            return sibRef;
        }

        @Override
        protected void setI(ItemSet I) {
            this.I = I;
        }

        @Override
        boolean hasSiblings() {
            return sibRef != null;
        }

        @Override
        protected void setSibRef(Node sibRef) {
            this.sibRef = sibRef;
        }

        @Override
        public String toString() {
            return String.format("{ sup: %d, I: %s }", sup, I.toString());
        }

    }
    
    public Dataset getDataset() {
        return dataset;
    }
    
    public int[] getNodeCardinalityCounts() {
        return nodeCardinalityCounts;
    }

    public NodeTop[] getStart() {
        return start;
    }
    public PTreeTable getPTreeTable() {
        return pTreeTable;
    }

}
