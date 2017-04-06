package PTree;

import HelperObjects.Dataset;

public class PTreeTable {
    
    private Dataset dataset;
    private PTree ptree;
    private Record[][] start;
    
    private static class Record {
        
    }
    
    public PTreeTable(PTree ptree) {
        this.ptree = ptree;
        this.dataset = ptree.getDataset();
        this.start = new Record[this.dataset.getTable().size()][];
        createPTreeTable();
    }

    private void createPTreeTable() {
        //dataset.getIn
    }

}
