package TTree;

import java.util.List;

/**
 * Created by Jonathan McDevitt on 2017-03-24.
 */
public class TTree {
    
    private List<Node> topLevel;
    private Dataset dataset;    
    
    private class Node {
        private int sup;
        private List<Node> children;
    }
}
