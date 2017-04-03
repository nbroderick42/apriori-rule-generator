package HelperObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ItemSet {
    
    private List<Integer> items;
    
    public ItemSet() {
        items = new ArrayList<>();
    }
    
    public ItemSet(ItemSet is) {
        items = new ArrayList<>(is.items);
        Collections.sort(items);
    }

    public ItemSet(Collection<Integer> items) {
        this.items = new ArrayList<>(items);
        Collections.sort(this.items);
    }

    public List<Integer> getItems() {
        return items;
    }
    
    public Integer get(int idx) {
        return items.get(idx);
    }
    
    public ItemSet append(int i) {
        items.add(i);
        return this;
    }
    
    public ItemSet append(ItemSet is) {
        is.items.forEach(this::append);
        return this;
    }
    
    public int size() {
        return items.size();
    }

    public Integer remove(int n) {
        return items.remove(n);
    }

    public boolean equals(ItemSet that) {
        for(int i = 0; i < items.size(); i++) {
            if (this.get(i).equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }
}
