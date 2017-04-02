package PTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import HelperObjects.Dataset;
import HelperObjects.Itemset;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class PTree {

    private Dataset dataset;
    private NodeTop[] start;

    private static abstract class Node {
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

        abstract Itemset getI();

        abstract void setI(Itemset I);

        abstract Node getSibRef();

        abstract void setSibRef(Node sibRef);

        abstract boolean hasSiblings();
    }

    private static class NodeTop extends Node {

        private NodeTop() {
            sup = 1;
        }

        @Override
        protected Itemset getI() {
            return Itemset.EMPTY;
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
        protected void setI(Itemset I) {
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

    private static class NodeInternal extends Node {
        private Itemset I;
        private Node sibRef;

        private NodeInternal(Itemset I, int sup) {
            this.I = I;
            this.sup = sup;
        }

        private NodeInternal(Itemset I) {
            this.I = I;
        }

        @Override
        public Itemset getI() {
            return I;
        }

        @Override
        public Node getSibRef() {
            return sibRef;
        }

        @Override
        protected void setI(Itemset I) {
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

    private enum LinkFlag {
        CHILD {
            @Override
            public void link(Node n1, Node n2) {
                n1.setChdRef(n2);
            }
        },
        SIBLING {
            @Override
            public void link(Node n1, Node n2) {
                n1.setSibRef(n2);
            }
        };

        public abstract void link(Node n1, Node n2);
    }

    public PTree(Dataset dataset) {
        this.dataset = dataset;
        this.start = new NodeTop[dataset.getValueRangeSet().size()];

        createPtree();
    }

    private void createPtree() {
        List<List<Integer>> R = dataset.getTable();
        R.forEach(this::addToPtreeTopLevel);
    }

    private void addToPtreeTopLevel(List<Integer> r) {
        r = new ArrayList<Integer>(r);
        Collections.sort(r);
        Itemset is = new Itemset(r);

        int r0 = r.get(0);
        if (start[r0] == null) {
            start[r0] = new NodeTop();
        } else {
            start[r0].sup++;
        }

        if (r.size() > 1) {
            addToPtree(LinkFlag.CHILD, start[r0].chdRef, Itemset.del1(is), start[r0]);
        }
    }

    private void addToPtree(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        if (ref == null) {
            Node newRef = new NodeInternal(r, 1);
            f.link(oldRef, newRef);
        } else {
            if (ref.getI().equals(r)) {
                ref.sup++;
                return;
            }

            boolean rLessThanRef = ref.getI().greaterThan(r);
            boolean rGreaterThanRef = !rLessThanRef;
            boolean rContainedInRef = ref.getI().contains(r);
            boolean rContainsRef = ref.getI().containedBy(r);

            if (rLessThanRef && rContainedInRef) {
                parent(f, ref, r, oldRef);
            } else if (rLessThanRef && !rContainedInRef) {
                eldSib(f, ref, r, oldRef);
            } else if (rGreaterThanRef && rContainsRef) {
                child(ref, r);
            } else if (rGreaterThanRef && !rContainsRef) {
                yngSib(f, ref, r, oldRef);
            } else {
                assert false : "Impossible case reached in addToPtree";
            }
        }
    }

    private void parent(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        NodeInternal newRef = new NodeInternal(r, ref.sup + 1);
        newRef.chdRef = ref;
        f.link(newRef, oldRef);
        ref.setI(Itemset.delN(ref.getI(), r));
        moveSiblings(ref, newRef);
    }

    private void child(Node ref, Itemset r) {
        ref.incSup();
        if (!ref.hasChild()) {
            NodeInternal newRef = new NodeInternal(Itemset.delN(r, ref.getI()), 1);
            ref.setChdRef(newRef);
        } else {
            addToPtree(LinkFlag.CHILD, ref.getChdRef(), Itemset.delN(r, ref.getI()), ref);
        }
    }

    private void eldSib(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        Itemset lss = Itemset.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = new NodeInternal(lss, ref.getSup() + 1);
            f.link(newPref, oldRef);
            r = Itemset.delN(r, lss);
            newPref.setChdRef(new NodeInternal(r, 1));
            newPref.getChdRef().setSibRef(ref);
            moveSiblings(ref, newPref);
        } else {
            NodeInternal newSref = new NodeInternal(r);
            newSref.setSibRef(ref);
            f.link(newSref, oldRef);
        }
    }

    private void yngSib(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        if (!ref.hasSiblings()) {
            yngSib1(f, ref, r, oldRef);
        } else {
            yngSib2(f, ref, r, oldRef);
        }
    }

    private void yngSib1(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        Itemset lss = Itemset.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = new NodeInternal(lss, ref.getSup() + 1);
            f.link(newPref, oldRef);
            ref.setI(Itemset.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            ref.setSibRef(new NodeInternal(Itemset.delN(r, lss), 1));
        } else {
            ref.setSibRef(new NodeInternal(r, 1));
        }
    }

    private void yngSib2(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        Itemset lss = Itemset.lss(r, ref.getI());
        if (!lss.isEmpty() && !lss.equals(oldRef.getI())) {
            NodeInternal newPref = new NodeInternal(lss, ref.getSup() + 1);
            f.link(newPref, oldRef);
            ref.setI(Itemset.delN(ref.getI(), lss));
            newPref.setChdRef(ref);
            Node tempRef = ref.getSibRef();
            ref.setSibRef(new NodeInternal(r, 1));
            ref.getSibRef().setSibRef(tempRef);
            moveSiblings(ref, newPref);
        } else {
            addToPtree(LinkFlag.SIBLING, ref.getSibRef(), r, ref);
        }
    }

    private static void moveSiblings(Node from, Node to) {
        if (from.hasSiblings()) {
            to.setSibRef(from.getSibRef());
            from.setSibRef(null);
        }
    }

}
