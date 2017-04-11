package TTree;

public class TTreeNode {

    private TTreeNode[] chdRef;
    private int sup = 0;

    public TTreeNode[] getChdRef() {
        return chdRef;
    }

    public int getSup() {
        return sup;
    }

    public boolean hasChildren() {
        if (chdRef == null) {
            return false;
        }
        for (TTreeNode n : this.chdRef) {
            if (n != null) {
                return true;
            }
        }
        return false;
    }

    public void incSup(int sup) {
        this.sup += sup;
    }

    public void setChdRef(TTreeNode[] chdRef) {
        this.chdRef = chdRef;
    }
}