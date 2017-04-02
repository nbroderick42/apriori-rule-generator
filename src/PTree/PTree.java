package PTree;

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

    private static class NodeTop {
        // TODO: Double-check that starting at 1 is the right thing to do
        private int sup = 1;
        private Node chdRef;
    }

    private static class Node {
        private int sup;
        private Itemset I;
        private Node chdRef;
        private Node sibRef;

        private Node(Itemset I, int sup) {
            this.I = I;
            this.sup = sup;
        }
    }

    private enum LinkFlag {
        TOP_LEVEL {
            @Override
            public void link(Node n1, Node n2) {
                // TODO Auto-generated method stub

            }
        },
        CHILD {
            @Override
            public void link(Node n1, Node n2) {
                // TODO Auto-generated method stub

            }
        },
        SIBLING {
            @Override
            public void link(Node n1, Node n2) {
                // TODO Auto-generated method stub

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
        Collections.sort(r);
        Itemset is = new Itemset(r);

        int r0 = r.get(0);
        if (start[r0] == null) {
            start[r0] = new NodeTop();
        } else {
            start[r0].sup++;
        }

        if (r.size() > 1) {
            addToPtree(LinkFlag.TOP_LEVEL, start[r0].chdRef, is.del1(), null);
        }
    }

    private void addToPtree(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        if (ref == null) {
            Node newRef = new Node(r, 1);
            f.link(oldRef, newRef);
        } else {
            if (ref.I.equals(r)) {
                ref.sup++;
                return;
            }

            boolean rLessThanRef = ref.I.greaterThan(r);
            boolean rGreaterThanRef = !rLessThanRef;
            boolean rContainedInRef = ref.I.contains(r);
            boolean rContainsRef = ref.I.containedBy(r);

            if (rLessThanRef && rContainedInRef) {
                parent(f, ref, r, oldRef);
            } else if (rLessThanRef && !rContainedInRef) {
                eldSib(f, ref, r, oldRef);
            } else if (rGreaterThanRef && rContainsRef) {
                child(ref, r);
            } else if (rGreaterThanRef && !rContainsRef) {
                yngSib(f, ref, r, oldRef);
            }
            
            assert false : "Impossible case reached in addToPtree";
        }
    }

    private void parent(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        Node newRef = new Node(r, ref.sup + 1);
        newRef.chdRef = ref;
        f.link(newRef, oldRef);
        //ref.I = 
    }

    private void eldSib(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        // TODO Auto-generated method stub

    }

    private void child(Node ref, Itemset r) {
        // TODO Auto-generated method stub

    }

    private void yngSib(LinkFlag f, Node ref, Itemset r, Node oldRef) {
        // TODO Auto-generated method stub

    }

}
