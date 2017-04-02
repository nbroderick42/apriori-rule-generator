package HelperObjects;

import java.util.ArrayList;
import java.util.List;

public class Itemset {
    
    private List<Integer> items;
    
    public Itemset() {
        items = new ArrayList<>();
    }
    
    public Itemset(Itemset is) {
        items = new ArrayList<>(is.items);
    }
    
    public Integer get(int idx) {
        return items.get(idx);
    }
    
    public Itemset append(int i) {
        items.add(i);
        return this;
    }
    
    public Itemset append(Itemset is) {
        is.items.forEach(this::append);
        return this;
    }
    
    public int size() {
        return items.size();
    }

    public Integer remove(int n) {
        return items.remove(n);
    }
}
