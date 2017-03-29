package TTree;

import java.util.ArrayList;
import java.util.List;

import HelperObjects.Dataset;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class TTree {

    private List<Node> start;
    private Dataset dataset;
    private boolean isNewLevel;

    private class Node {
        private int sup;
        private List<Node> children;
    }

    public TTree(Dataset dataset) {
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
        dataset.getValueRangeSet().forEach(val -> start.add(new Node()));
        dataset.getTable().forEach(r -> {
            r.forEach(s -> {
                start.get(s).sup++;
            });
        });

    }

    private void prune(List<Node> ref, int k) {
        // TODO Auto-generated method stub

    }

    private void genLevelN(List<Node> ref, int k, int newK, List<Node> I) {
        // TODO Auto-generated method stub

    }

    private void addSupport(int k) {
        // TODO Auto-generated method stub

    }

    private void addSup(List<Node> ref, int k, int end, int r) {
        // TODO Implement
    }

    private void genLevel(List<Node> ref, int end, List<Node> I) {

    }

}
