package PTree;

import java.io.IOException;

import DataSource.DataSource;
import DataSource.ItemSet;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class PTree {
    
    private DataSource dataset;
    private int[] nodeCardinalityCounts;
    private PTreeTable pTreeTable;

    private PTreeNodeTop[] start;

    public PTree(DataSource dataset) throws IOException {
        this.dataset = dataset;
        this.start = new PTreeNodeTop[dataset.getNumUniqueItems() + 1];
        this.nodeCardinalityCounts = new int[dataset.getNumUniqueItems() + 1];

        createPtree();

        this.pTreeTable = new PTreeTable(this);
    }

    private void addToPtree(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        if (ref == null) {
            PTreeNode newRef = createPTreeInternalNode(r, 1, itemSetLength);
            link.accept(oldRef, newRef);
        } else {
            if (ref.getI().equals(r)) {
                ref.sup++;
                return;
            }

            boolean rLessThanRef = ref.getI().greaterThan(r);
            boolean rContainedInRef = ref.getI().contains(r);
            boolean rContainsRef = ref.getI().containedBy(r);

            if (rLessThanRef && rContainedInRef) {
                parent(link, ref, r, oldRef, parentLength, itemSetLength);
            } else if (rLessThanRef) {
                eldSib(link, ref, r, oldRef, parentLength, itemSetLength);
            } else if (rContainsRef) {
                child(ref, r, parentLength, itemSetLength);
            } else {
                yngSib(link, ref, r, oldRef, parentLength, itemSetLength);
            }
        }
    }

    private void addToPtreeTopLevel(ItemSet is) {
        if (is.isEmpty()) {
            return;
        }

        int r0 = is.get(0);
        if (start[r0] == null) {
            start[r0] = new PTreeNodeTop(r0);
            nodeCardinalityCounts[1]++;
        } else {
            start[r0].sup++;
        }

        if (is.size() > 1) {
            addToPtree(PTreeNode::linkAsChd, start[r0].chdRef, ItemSet.del1(is), start[r0], 2, is.size());
        }
    }

    private void child(PTreeNode ref, ItemSet r, int parentLength, int itemSetLength) {
        ref.incSup(1);
        if (!ref.hasChild()) {
            PTreeNode newRef = createPTreeInternalNode(ItemSet.delN(r, ref.getI()), 1, itemSetLength);
            ref.setChdRef(newRef);
        } else {
            addToPtree(PTreeNode::linkAsChd, ref.getChdRef(), ItemSet.delN(r, ref.getI()), ref,
                    parentLength + ref.getI().size(), itemSetLength);
        }
    }

    private void createPtree() throws IOException {
        dataset.forEach(this::addToPtreeTopLevel);
    }

    private PTreeNodeInternal createPTreeInternalNode(ItemSet I, int sup, int level) {
        nodeCardinalityCounts[level]++;
        return new PTreeNodeInternal(I, sup);
    }

    private void eldSib(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty()) {
            PTreeNodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1);
            link.accept(oldRef, newPref);
            r = ItemSet.delN(r, lss);
            newPref.setChdRef(createPTreeInternalNode(r, 1, itemSetLength));
            ref.setI(ItemSet.delN(ref.getI(), lss));
            newPref.getChdRef().setSibRef(ref);
            newPref.setSibRef(ref.getSibRef());
            ref.setSibRef(null);
        } else {
            PTreeNodeInternal newSref = createPTreeInternalNode(r, 1, itemSetLength);
            newSref.setSibRef(ref);
            link.accept(oldRef, newSref);
        }
    }

    public DataSource getDataset() {
        return dataset;
    }

    public int[] getNodeCardinalityCounts() {
        return nodeCardinalityCounts;
    }

    public PTreeTable getPTreeTable() {
        return pTreeTable;
    }

    public PTreeNodeTop[] getStart() {
        return start;
    }

    private void parent(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        PTreeNode newRef = createPTreeInternalNode(r, ref.getSup() + 1, itemSetLength);
        newRef.setChdRef(ref);
        link.accept(oldRef, newRef);
        ref.setI(ItemSet.delN(ref.getI(), r));
        
        newRef.setSibRef(ref.getSibRef());
        ref.setSibRef(null);
    }

    private void yngSib(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        if (!ref.hasSiblings()) {
            yngSib1(link, ref, r, oldRef, parentLength, itemSetLength);
        } else {
            yngSib2(link, ref, r, oldRef, parentLength, itemSetLength);
        }
    }

    private void yngSib1(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty()) {
            PTreeNodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1);
            link.accept(oldRef, newPref);
            ref.setI(ItemSet.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            ref.setSibRef(createPTreeInternalNode(ItemSet.delN(r, lss), 1, itemSetLength));
        } else {
            ref.setSibRef(createPTreeInternalNode(r, 1, itemSetLength));
        }
    }

    private void yngSib2(PTreeNode.LinkFunction link, PTreeNode ref, ItemSet r, PTreeNode oldRef, int parentLength,
            int itemSetLength) {
        ItemSet lss = ItemSet.lss(r, ref.getI());
        if (!lss.isEmpty()) {
            PTreeNodeInternal newPref = createPTreeInternalNode(lss, ref.getSup() + 1, lss.size() + parentLength - 1);
            link.accept(oldRef, newPref);
            ref.setI(ItemSet.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            PTreeNode tempRef = ref.getSibRef();
            r = ItemSet.delN(r, lss);
            ref.setSibRef(createPTreeInternalNode(r, 1, itemSetLength));
            newPref.setSibRef(tempRef);
        } else {
            addToPtree(PTreeNode::linkAsSib, ref.getSibRef(), r, ref, parentLength, itemSetLength);
        }
    }

}
