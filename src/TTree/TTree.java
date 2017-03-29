package TTree;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import HelperObjects.Dataset;

/*
 * Algorithm variables
 * 
 * R - input dataset
 * N - number of columns/attributes
 * D - number of records
 * ref - reference to current node
 */

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class TTree {

    private List<Node> start;
    private Dataset dataset;
    private boolean isNewLevel;
    private int minSupport;

    private class Node {
        private int sup = 0;
        private List<Node> children;
        private int val;
        
        public Node(int val) {
            this.val = val;
        }
    }

    public TTree(Dataset dataset, int minSupport) {
        this.dataset = dataset;
        this.minSupport = minSupport;

        createTtree();
    }

    private void createTtree() {
        createTTreeTopLevel(dataset);
        prune(start, 1);
        genLevelN(start, 1, 1, null);
        int k = 2;

        do {
            addSupport(k);
            prune(start, k);
            isNewLevel = false;
            genLevelN(start, 1, k, new ArrayList<>());
            k++;
        } while (isNewLevel);
    }

    private void createTTreeTopLevel(Dataset dataset) {
        dataset.getValueRangeSet().forEach(val -> start.add(new Node(val)));
        dataset.getTable().forEach(r -> r.forEach(s -> start.get(s).sup++));
    }

    private void prune(List<Node> ref, int k) {
        if (k == 1) {
            for (Node t : ref) {
                if (t != null && t.sup < minSupport) {
                    // Make sure this isn't broken
                    ref.remove(t);
                }
            }
        }
        else {
            for (Node t : ref) {
                if (t != null && t.children != null) {
                    prune(t.children, k - 1);
                }
            }
        }
    }

    private void genLevelN(List<Node> ref, int k, int newK, List<Integer> I) {
        if (k == newK) {
            for (int i = 2; i < ref.size(); i++) {
                if (ref.get(i) != null) {
                    I.add(i);
                    genLevel(ref, i, I);
                }
            }
        }
        else {
            for (int i = 2; i < ref.size(); i++) {
                if (ref.get(i) != null) {
                    I.add(i);
                    genLevelN(ref.get(i).children, k + 1, newK, I);
                }
            }
        }
    }

    private void addSupport(int k) {
        for (List<Integer> r : dataset.getTable()) {
            addSup(start, k, r.size(), r);
        }
    }

    private void addSup(List<Node> ref, int k, int end, List<Integer> r) {
        if (k == 1) {
            for (int si : r) {
                // Make better later (single var)
                if (ref.get(si) != null) {
                    ref.get(si).sup++;
                }
            }
        }
        else {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                // Make better later (single var)
                if (ref.get(si) != null) {
                    addSup(ref.get(si).children, k - 1, i, r);
                }
            }
        }
    }

    private void genLevel(List<Node> ref, int end, List<Integer> I) {
        ref.get(end).children = new ArrayList<>();
        for (int i = 1; i < end; i++) {
            if (ref.get(i) != null) {
                List<Integer> newI = new ArrayList<>(I);
                newI.add(i);
                if (testCombinations(newI)) {
                    ref.get(end).children.set(i, new Node(i));
                    isNewLevel = true;
                }
                else {
                    ref.get(end).children.set(i, null);
                }
            }
        }
    }

    private boolean testCombinations(List<Integer> I) {
        if (I.size() < 3) {
            return true;
        }
        
        List<Integer> I1 = new ArrayList<>();
        I1.add(I.get(1));
        I1.add(I.get(0));
        
        List<Integer> I2 = new ArrayList<>(I);
        I2.remove(2);
        
        return combinations(null, 0, 2, I1, I2);
    }

    private boolean combinations(List<Integer> I, int start, int end, List<Integer> I1, List<Integer> I2) {
        if (end > I2.size()) {
            List<Integer> testSet = new ArrayList<>(I);
            I.addAll(I1);
            return findInTree(testSet);
        }
        else {
            for (int i = start; i < end; i++) {
                List<Integer> tempSet = new ArrayList<>(I);
                tempSet.add(I2.get(i));
                if (!combinations(tempSet, i + 1, end + 1, I1, I2)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean findInTree(List<Integer> testSet) {
        return findInTree(testSet, testSet.size() - 1, start);
    }
    
    private boolean findInTree(List<Integer> testSet, int i, List<Node> ref) {
        if (i < 0) {
            return true;
        }
        else {
            ListIterator<Node> li = ref.listIterator(ref.size());
            while (li.hasPrevious()) {
                Node n = li.previous();
                if (n.val == testSet.get(i)) {
                    return findInTree(testSet, i - 1, n.children);
                }
            }
            return false;
        }
    }

}
