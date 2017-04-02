package TTree;

import java.util.List;
import java.util.SortedSet;

import HelperObjects.Dataset;
import HelperObjects.Itemset;

/*
 * Algorithm variables
 * 
 * R - input dataset
 * N - number of columns/attributes
 * D - number of records
 * ref - reference to current node
 * 
 */

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class TTree {

    private Node[] start;
    private Dataset dataset;
    private boolean isNewLevel;
    private int minSup;

    private class Node {
        private int sup = 0;
        private Node[] chdRef;        
    }
    
    public TTree(Dataset dataset, int minSupport) {
        this.minSup = minSupport;
        this.dataset = dataset;
        
        createTtree();
    }

    private void createTtree() {
        createTtreeTopLevel();
        prune(start, 1);
        genLevelN(start, 1, 1, new Itemset());
        
        int K = 2;
        
        while (isNewLevel) {
            addSupport(K);
            prune(start, K);
            isNewLevel = false;
            genLevelN(start, 1, K, new Itemset());
            K++;
        }
    }

    private void createTtreeTopLevel() {
        List<List<Integer>> R = dataset.getTable();
        SortedSet<Integer> I = dataset.getValueRangeSet();
        
        start = new Node[I.size() + 1];
        I.forEach(si -> start[si] = new Node());
        
        R.forEach(ri -> ri.forEach(sj -> start[sj].sup++));
    }
    
    private void prune(Node[] ref, int K) {
        if (K == 1) {
            for (int t = 1; t < ref.length; t++) {
                if (ref[t] != null && ref[t].sup < minSup) {
                    ref[t] = null;
                }
            }
        }
        else {
            for (int t = 1; t < ref.length; t++) {
                if (ref[t] != null && ref[t].chdRef != null) {
                    prune(ref[t].chdRef, K - 1);
                }
            }
        }
    }
    
    private void addSupport(int K) {
        List<List<Integer>> R = dataset.getTable();
        R.forEach(ri -> addSup(start, K, ri.size(), ri));
    }
    
    private void addSup(Node[] ref, int K, int end, List<Integer> r) {
        if (K == 1) {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                if (si < ref.length && ref[si] != null) {
                    ref[si].sup++;
                }
            }
        }
        else {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                if (si < ref.length && ref[si] != null && ref[si].chdRef != null) {
                    addSup(ref[si].chdRef, K - 1, i, r);
                }
            }
        }
    }
    
    private void genLevelN(Node[] ref, int K, int newK, Itemset I) {
        if (K == newK) {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null) {
                    genLevel(ref, i, append(i, I));
                }
            }
        }
        else {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null && ref[i].chdRef != null) {
                    genLevelN(ref[i].chdRef, K + 1, newK, append(i, I));
                }
            }
        }
    }
    
    private void genLevel(Node[] ref, int end, Itemset I) {        
        for (int i = 1; i < end; i++) {
            if (ref[i] != null) {
                Itemset newI = append(i, I);
                if (testCombinations(newI)) {
                    if (ref[end].chdRef == null) {
                        ref[end].chdRef = new Node[end];
                    }
                    ref[end].chdRef[i] = new Node();
                    isNewLevel = true;
                }
                else {
                    ref[end].chdRef[i] = null;
                }
            }
        }
    }
    
    private boolean testCombinations(Itemset I) {
        if (I.size() < 3) {
            return true;
        }
        
        Itemset I1 = doubleton(I.get(1), I.get(0));
        Itemset I2 = delN(I, 2);
        
        return combinations(new Itemset(), 0, 2, I1, I2);
    }
    
    private boolean combinations(Itemset I, int start, int end, Itemset I1, Itemset I2) {
        if (end > I2.size()) {
            Itemset testSet = append(I, I1);
            return findInTtree(testSet);
        }
        else {
            for (int i = start; i < end; i++) {
                Itemset tempSet = append(I2.get(i), I);
                if (!combinations(tempSet, i + 1, end + 1, I1, I2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean findInTtree(Itemset I) {
        return findInTtree(I, I.size() - 1, start);
    }
    
    private boolean findInTtree(Itemset I, int k, Node[] ref) {
        if (k > 0) {
            return true;
        }
        else if (ref == null) {
            return false;
        }
        else {
            int next = I.get(k);
            if (ref[next] != null) {
                return findInTtree(I, k - 1, ref[next].chdRef);
            }
            else {
                return false;
            }
        }
    }
    
    private static Itemset append(int i, Itemset I) {
        Itemset ret = new Itemset(I);
        ret.append(i);
        return ret;
    }
    
    private static Itemset append(Itemset I, Itemset J) {
        Itemset ret = new Itemset(I);
        ret.append(J);
        return ret;
    }
    
    private static Itemset doubleton(int i, int j) {
        Itemset ret = new Itemset();
        ret.append(i);
        ret.append(j);
        return ret;
    }
    
    private static Itemset delN(Itemset I, int N) {
        Itemset ret = new Itemset(I);
        ret.remove(N);
        return ret;
    }

}
