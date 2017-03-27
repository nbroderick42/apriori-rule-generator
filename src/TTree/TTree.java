package TTree;

import HelperObjects.Dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class TTree {

    private List<Node> start;

    private class Node {
        private int sup;
        private List<Node> children;
    }

    private TTree() {

    }

    public static TTree createTtree(Dataset dataset) {
        TTree tree = new TTree();
        tree.createTTreeTopLevel(dataset);

        tree.prune(1);
        tree.genLevelN(1, 1, null);
        int k = 2;
        
        boolean isNewLevel = false;
        do {
            tree.addSupport(dataset, k);
            tree.prune(k);
            isNewLevel = tree.genLevelN(1, k, new ArrayList<>());
            k++;
        } while (isNewLevel);
        
        return tree;
    }

    private void createTTreeTopLevel(Dataset dataset) {
        dataset.getValueRangeSet().forEach(val -> start.add(new Node()));
        dataset.getTable().forEach(r -> {
            r.forEach(s -> {
                start.get(s).sup++;
            });
        });
        
    }

    private void prune(int k) {
        // TODO Auto-generated method stub

    }
    
    private boolean genLevelN(int i, int j, List<Node> list) {
        return false;
        
    }
    
    private void addSupport(Dataset dataset, int k) {
        // TODO Auto-generated method stub
        
    }

}
