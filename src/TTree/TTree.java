package TTree;

import java.util.ArrayList;
import java.util.Collections;
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

    private Node[] start;
    private Dataset dataset;
    private boolean isNewLevel;
    private int minSupport;

    private class Node {
        private int sup = 0;
        private Node[] children;
        public Node() {}
        
    }

    public TTree(Dataset dataset, int minSupport) { 
        this.dataset = dataset;
        this.minSupport = minSupport;

        createTtree();
    }

    private void createTtree() {
        createTTreeTopLevel(dataset);
        prune(start, 1);
        genLevelN(start, 1, 1, new ArrayList<>());
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
    	start = new Node[dataset.getValueRangeSet().size()];
    	for(int i = 0; i < start.length; i++) {
    		start[i] = new Node();
    	}
        
        dataset.getTable().forEach(r -> r.forEach(s -> start[s].sup++));
    }

    private void prune(Node[] start, int k) {
        if (k == 1) {
            for (int i = 0; i < start.length; i++) {
                if (start[i] != null && start[i].sup < minSupport) {
                    // Make sure this isn't broken
                    start[i] = null;
                }
            }
        }
        else {
            for (Node t : start) {
                if (t != null && t.children != null) {
                    prune(t.children, k - 1);
                }
            }
        }
    }

    private void genLevelN(Node[] ref, int k, int newK, List<Integer> I) {
        if (k == newK) {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null) {
                    I.add(i);
                    genLevel(ref, i, I);
                }
            }
        }
        else {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null) {
                    I.add(i);
                    genLevelN(ref[i].children, k + 1, newK, I);
                }
            }
        }
    }

    private void addSupport(int k) {
        for (List<Integer> r : dataset.getTable()) {
            addSup(start, k, r.size(), r);
        }
    }

    private void addSup(Node[] ref, int k, int end, List<Integer> r) {
        if (k == 1) {
            for (int si : r) {
                // Make better later (single var)
                if (ref[si] != null) {
                    ref[si].sup++;
                }
            }
        }
        else {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                // Make better later (single var)
                if (ref[si] != null) {
                    addSup(ref[si].children, k - 1, i, r);
                }
            }
        }
    }

    private void genLevel(Node[] ref, int end, List<Integer> I) {
        for (int i = 1; i < end; i++) {
            if (ref[i] != null) {
                List<Integer> newI = new ArrayList<>(I);
                newI.add(i);
                if (testCombinations(newI)) {
                    ref[end].children[i] = new Node();
                    isNewLevel = true;
                }
                else {
                    ref[end].children[i] =  null;
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
        
        return combinations(Collections.emptyList(), 0, 2, I1, I2);
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
    
    private boolean findInTree(List<Integer> testSet, int end, Node[] ref) {
        if (end < 0) {
            return true;
        }
        else {
            int i = 0;
            while(i < ref.length && testSet.get(i) != ref[i].sup) {
            	i++;
            }
            return false;
        }
    }

}
