package TTree;

public class TTreeNode {

    private int sup = 0;
    private TTreeNode[] chdRef;

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

    public int getSup() {
        return sup;
    }

    public void incSup(int sup) {
        this.sup += sup;
    }

    public TTreeNode[] getChdRef() {
        return chdRef;
    }

    public void setChdRef(TTreeNode[] chdRef) {
        this.chdRef = chdRef;
    }
}