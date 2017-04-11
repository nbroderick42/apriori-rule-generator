package TTree;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import HelperObjects.DataFileHandle;
import HelperObjects.ItemSet;
import HelperObjects.Rule;
import HelperObjects.RuleGenerator;
import PTree.PTree;
import PTree.PTreeTable;

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
public class TTree implements RuleGenerator {

    private Node[] start;
    private DataFileHandle dataset;
    private boolean isNewLevel;
    private int minSup;

    private class Node {

        private int sup = 0;
        private Node[] chdRef;

        public boolean hasChildren() {
            if (chdRef == null) {
                return false;
            }
            for (Node n : this.chdRef) {
                if (n != null) {
                    return true;
                }
            }
            return false;
        }
    }

    public static TTree fromDataset(DataFileHandle dataset, double minSup) throws IOException {
        TTree result = new TTree(dataset, minSup);
        return result.createTtree();
    }

    public static TTree fromPTree(DataFileHandle dataset, double minSup) throws IOException {
        TTree result = new TTree(dataset, minSup);
        return result.createFromPTree();
    }

    private TTree(DataFileHandle dataset, double minSup) {
        this.minSup = convertToIntegerNumerator(minSup, dataset.getNumRecords());
        this.dataset = dataset;
    }
    
    private static int convertToIntegerNumerator(double d, int size) {
        return (int) Math.ceil(d * size);
    }

    public TTree createTtree() throws IOException {
        createTtreeTopLevel();
        genLevelN(start, 1, new ItemSet());

        int K = 2;

        while (isNewLevel) {
            addSupport(K);
            prune(start, K);
            isNewLevel = false;
            genLevelN(start, K, new ItemSet());
            K++;
        }

        return this;
    }

    public TTree createFromPTree() throws IOException {
        PTree pTree = new PTree(dataset);
        createTtreeTopLevel(pTree);
        genLevelN(start, 1, new ItemSet());
        createTtreeLevelN(pTree);

        return this;
    }

    private void createTtreeLevelN(PTree pTree) {
        int k = 2;

        while (isNewLevel) {
            addSupportToTTreeLevelN(pTree, k);
            prune(start, k);
            isNewLevel = false;
            genLevelN(start, k, new ItemSet());
            k++;
        }
    }

    private void addSupportToTTreeLevelN(PTree pTree, int level) {
        PTreeTable.Record[][] table = pTree.getPTreeTable().getStart();
        int[] cardinalityCounts = pTree.getNodeCardinalityCounts();
        addSupportToTTreeLevelN(table, cardinalityCounts, level);
    }
    
    private void addSupportToTTreeLevelN(PTreeTable.Record[][] table, int[] cardinaltyCounts, int level) {
        for (int i = level; i < table.length; i++) {
            if (table[i] != null) {
                int count = cardinaltyCounts[i];
                for (int j = 0; j < count; j++) {
                    PTreeTable.Record r = table[i][j];
                    addSupportToTTreeLevelN(start, level, r.getLabel(), r.getAncestors(), r.getSup());
                }
            }
        }
    }

    private void addSupportToTTreeLevelN(Node[] ref, int level, ItemSet label, ItemSet anc, int sup) {
        int len = ref.length;

        if (level == 1) {
            for (int item : anc) {
                if (item >= len) {
                    break;
                }
                if (ref[item] != null) {
                    ref[item].sup += sup;
                }
            }
        } else {
            for (int item : label) {
                if (item >= len) {
                    break;
                }
                if (ref[item] != null && ref[item].chdRef != null) {
                    addSupportToTTreeLevelN(ref[item].chdRef, level - 1, anc, anc, sup);
                }
            }
        }
    }

    private void initTopLevelNodes() throws IOException {
        List<Node> topLevel = new ArrayList<>();
        dataset.forEach(is -> topLevel.add(new Node()));
        
        start = new Node[dataset.getNumRecords() + 1];
        topLevel.toArray(start);
    }

    private void createTtreeTopLevel() throws IOException {
        initTopLevelNodes();
        dataset.forEach(ri -> ri.forEach(sj -> start[sj].sup++));
        prune(start, 1);
    }

    private void createTtreeTopLevel(PTree pTree) throws IOException {
        initTopLevelNodes();

        PTreeTable.Record[][] records = pTree.getPTreeTable().getStart();

        for (int i = 1; i < records.length; i++) {
            PTreeTable.Record[] level = records[i];
            if (level != null) {
                for (int j = 0; j < level.length; j++) {
                    ItemSet label = level[j].getLabel();
                    int sup = level[j].getSup();
                    for (int k = 0; k < label.size(); k++) {
                        start[label.get(k)].sup += sup;
                    }
                }
            }
        }
        prune(start, 1);
    }

    private boolean prune(Node[] ref, int K) {

        if (K == 1) {
            boolean allUnsupported = true;
            for (int t = 1; t < ref.length; t++) {
                if (ref[t] != null && ref[t].sup < minSup) {
                    ref[t] = null;
                } else if (ref[t] != null) {
                    allUnsupported = false;
                }
            }
            return allUnsupported;
        } else {
            for (int t = 1; t < ref.length; t++) {
                if (ref[t] != null && ref[t].chdRef != null) {
                    if (prune(ref[t].chdRef, K - 1)) {
                        ref[t].chdRef = null;
                    }
                }
            }
        }

        return false;
    }

    private void addSupport(int K) throws IOException {
        dataset.forEach(ri -> addSup(start, K, ri.size(), ri));
    }

    private void addSup(Node[] ref, int K, int end, ItemSet r) {
        if (K == 1) {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                if (si < ref.length && ref[si] != null) {
                    ref[si].sup++;
                }
            }
        } else {
            for (int i = 0; i < end; i++) {
                int si = r.get(i);
                if (si < ref.length && ref[si] != null && ref[si].chdRef != null) {
                    addSup(ref[si].chdRef, K - 1, i, r);
                }
            }
        }
    }

    private void genLevelN(Node[] ref, int K, ItemSet I) {
        if (K == 1) {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null) {
                    genLevel(ref, i, append(i, I));
                }
            }
        } else {
            for (int i = 2; i < ref.length; i++) {
                if (ref[i] != null && ref[i].chdRef != null) {
                    genLevelN(ref[i].chdRef, K - 1, append(i, I));
                }
            }
        }
    }

    private void genLevel(Node[] ref, int end, ItemSet I) {
        Node curr = ref[end];
        curr.chdRef = new Node[end];

        for (int i = 1; i < end; i++) {
            if (ref[i] != null) {
                ItemSet newI = append(i, I);
                if (testCombinations(newI)) {
                    curr.chdRef[i] = new Node();
                    isNewLevel = true;
                } else {
                    curr.chdRef[i] = null;
                }
            }
        }
    }

    private boolean testCombinations(ItemSet I) {
        if (I.size() < 3) {
            return true;
        }

        ItemSet I1 = ItemSet.doubleton(I.get(1), I.get(0));
        ItemSet I2 = ItemSet.delN(I, 2);

        return combinations(new ItemSet(), 0, 2, I1, I2);
    }

    private boolean combinations(ItemSet I, int start, int end, ItemSet I1, ItemSet I2) {
        if (end > I2.size()) {
            ItemSet testSet = append(I, I1);
            return findInTtree(testSet) != null;
        } else {
            for (int i = start; i < end; i++) {
                ItemSet tempSet = append(I2.get(i), I);
                if (!combinations(tempSet, i + 1, end + 1, I1, I2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Node findInTtree(ItemSet I) {
        return findInTtree(I, I.size() - 1, start, null);
    }

    private Node findInTtree(ItemSet I, int k, Node[] ref, Node prev) {
        if (k < 0) {
            return prev;
        } else if (ref == null) {
            return null;
        } else {
            int next = I.get(k);
            if (next < ref.length && ref[next] != null) {
                return findInTtree(I, k - 1, ref[next].chdRef, ref[next]);
            } else {
                return null;
            }
        }
    }

    private static ItemSet append(int i, ItemSet I) {
        ItemSet ret = new ItemSet(I);
        ret.append(i);
        return ret;
    }

    private static ItemSet append(ItemSet I, ItemSet J) {
        ItemSet ret = new ItemSet(I);
        ret.append(J);
        return ret;
    }

    @Override
    public List<Rule> generateRules(double confidence) {
        return generateRules(confidence, start, null, 0);
    }

    public List<Rule> generateRules(double confidence, Node[] layer, List<Integer> path, int level) {
        List<Rule> rules = new ArrayList<>();
        for (int i = 0; i < layer.length; i++) {
            /** If we are at the beginning, initialize the path */
            if (path == null) {
                path = new ArrayList<>();
            }
            Node n = layer[i];
            /**
             * If our value is not null, then we can begin to evaluate as a tree
             */
            if (n != null) {

                /**
                 * If we are not null, then we can add ourselves to the path and
                 * construct rules involving ourselves
                 */
                path.add(i);
                if (path.size() > 1) {
                    rules.addAll(generatePartitions(confidence, path));
                }

                /**
                 * If we have children, then we need to generate rules including
                 * these children
                 */
                if (n.hasChildren()) {
                    rules.addAll(generateRules(confidence, n.chdRef, path, level + 1));
                }
                /** Clear the path before moving to the next node */
                for (int j = path.size() - 1; j >= level; j--) {
                    path.remove(j);
                }
            }
        }
        return rules;
    }

    private List<Rule> generatePartitions(double minConf, List<Integer> path) {
        assert path.size() >= 2 : "Need at least two elements in path";

        int datasetSize = dataset.getNumRecords();
        double unionSup = (double) findInTtree(new ItemSet(path)).sup / (double) datasetSize;
        List<Rule> result = new ArrayList<>();

        BigInteger max = BigInteger.valueOf(1 << (path.size() - 1));

        for (BigInteger field = BigInteger.ONE; field.compareTo(max) < 0; field = field.add(BigInteger.ONE)) {
            ItemSet antecedent = new ItemSet();
            ItemSet consequent = new ItemSet();
            int j = 0;
            for (BigInteger i = BigInteger.ONE; max.shiftLeft(1).compareTo(i) > 0; i = i.shiftLeft(1), j++) {
                if (i.and(field).equals(BigInteger.ZERO)) {
                    antecedent.append(path.get(j));
                } else {
                    consequent.append(path.get(j));
                }
            }

            double anteSup = (double) findInTtree(antecedent).sup / datasetSize;
            double consSup = (double) findInTtree(consequent).sup / datasetSize;

            double anteConf = unionSup / anteSup;
            double consConf = unionSup / consSup;

            if (anteConf > minConf) {
                result.add(new Rule(dataset, antecedent, consequent, unionSup, anteConf));
            }
            if (consConf > minConf) {
                result.add(new Rule(dataset, consequent, antecedent, unionSup, consConf));
            }
        }

        return result;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb, start, 0);
        return sb.toString();
    }
    
    private void toString(StringBuilder sb, Node[] layer, int level) {
        if (layer != null) {
            String result = "";
            
            for (int i = 0; i < level; i++) result += "\t";
            for (int i = 0; i < layer.length; i++) result += (layer[i] != null ? i : "x") + " ";
            
            sb.append(result + "\n");
            
            for (int i = 0; i < layer.length; i++) {
                if (layer[i] != null) {
                    toString(sb, layer[i].chdRef, level + 1);
                }
            }
        }
    }
}
