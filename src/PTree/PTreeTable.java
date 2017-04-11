package PTree;

import DataSource.ItemSet;

public class PTreeTable {

    private int[] marker;
    private PTree ptree;
    private PTreeTableRecord[][] start;

    public PTreeTable(PTree ptree) {
        this.ptree = ptree;
        this.start = new PTreeTableRecord[ptree.getStart().length + 1][];
        this.marker = new int[ptree.getStart().length + 1];
        createPTreeTable();
    }

    private void addToTable(ItemSet ancestors, ItemSet label, int sup, int level) {
        PTreeTableRecord newRecord;

        if (ancestors.isEmpty()) {
            newRecord = new PTreeTableRecord(label, label, sup);
        } else {
            newRecord = new PTreeTableRecord(ancestors, ancestors.union(label), sup);
        }

        start[level][marker[level]] = newRecord;
        marker[level]++;
    }

    private void createPTreeTable() {
        PTreeNode[] pTreeStart = ptree.getStart();
        int[] nodeCardinalityCounts = ptree.getNodeCardinalityCounts();

        for (int i = 1; i < nodeCardinalityCounts.length; i++) {
            int cardinality = nodeCardinalityCounts[i];
            if (cardinality > 0) {
                start[i] = new PTreeTableRecord[cardinality];
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

    private void createPTreeTable(PTreeNode pTreeRef, ItemSet is, int currLevel) {
        if (pTreeRef != null) {
            int level = currLevel + pTreeRef.getI().size();

            addToTable(pTreeRef.getI(), is, pTreeRef.getSup(), level);

            createPTreeTable(pTreeRef.getChdRef(), is.union(pTreeRef.getI()), level);
            createPTreeTable(pTreeRef.getSibRef(), is, currLevel);
        }
    }

    public PTreeTableRecord[][] getStart() {
        return start;
    }

}
