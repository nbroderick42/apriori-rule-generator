package PTree;

import HelperObjects.ItemSet;
import PTree.PTree.Node;

public class PTreeTable {
    
    private PTree ptree;
    private Record[][] start;
    private int[] marker;
    
    public static class Record {
        private ItemSet label;
        private ItemSet ancestors;
        private int sup;
        
        private Record(ItemSet label, ItemSet ancestors, int sup) {
            this.label = label;
            this.ancestors = ancestors;
            this.sup = sup;
        }

        public ItemSet getLabel() {
            return label;
        }

        public ItemSet getAncestors() {
            return ancestors;
        }

        public int getSup() {
            return sup;
        }
    }
    
    public PTreeTable(PTree ptree) {
        this.ptree = ptree;
        this.start = new Record[ptree.getStart().length + 1][];
        this.marker = new int[ptree.getStart().length + 1];
        createPTreeTable();
    }

    private void createPTreeTable() {
        PTree.Node[] pTreeStart = ptree.getStart();
        int[] nodeCardinalityCounts = ptree.getNodeCardinalityCounts();
        
        for (int i = 1; i < nodeCardinalityCounts.length; i++) {
            int cardinality = nodeCardinalityCounts[i];
            if (cardinality > 0) {
                start[i] = new Record[cardinality];
            }
        }
        for (int i = 0; i < pTreeStart.length; i++) {
            if (pTreeStart[i] != null) {
                ItemSet is = new ItemSet(i);
                addToTable(ItemSet.EMPTY, is, pTreeStart[i].getSup(), 1);
                createPTreeTable(pTreeStart[i].chdRef, is, 1);
            }
        }
        
    }

    private void addToTable(ItemSet ancestors, ItemSet label, int sup, int level) {
        Record newRecord;
        
        if (ancestors.isEmpty()) {
            newRecord = new Record(label, label, sup);
        }
        else {
            newRecord = new Record(ancestors, ancestors.union(label), sup);
        }
        
        start[level][marker[level]] = newRecord;
        marker[level]++;
    }

    private void createPTreeTable(Node pTreeRef, ItemSet is, int currLevel) {
        if (pTreeRef != null) {
            int level = currLevel + pTreeRef.getI().size();
            
            addToTable(pTreeRef.getI(), is, pTreeRef.getSup(), level);
            
            createPTreeTable(pTreeRef.getChdRef(), is.union(pTreeRef.getI()), level);
            createPTreeTable(pTreeRef.getSibRef(), is, currLevel);
        }
    }

    public Record[][] getStart() {
        return start;
    }
    
    
}
