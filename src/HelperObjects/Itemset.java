package HelperObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Itemset {

    private List<Integer> items;

    public Itemset(Collection<Integer> c) {
        items = new ArrayList<>(c);
    }

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

    public boolean equals(List<Integer> r) {
        return items.equals(r);
    }

    public boolean greaterThan(Itemset r) {
        int i;
        for (i = 0; i < r.size() && i < items.size(); i++) {
            int si = items.get(i);
            int ri = r.get(i);

            if (si > ri) {
                return true;
            } else if (si < ri) {
                return false;
            }
        }
        return i < r.size();
    }

    public boolean contains(Itemset r) {
        return contains(items, r.items);
    }

    public boolean containedBy(Itemset r) {
        return contains(r.items, items);
    }

    private static boolean contains(List<Integer> l1, List<Integer> l2) {
        if (l2.isEmpty()) {
            return true;
        } else if (l1.isEmpty()) {
            return false;
        }

        int i = 0;
        int j = 0;
        int r = l1.get(0);
        int s = l2.get(0);
        int l1Size = l1.size();
        int l2Size = l2.size();

        while (i < l1Size && j < l2Size) {
            if (r < s && ++i < l1Size) {
                r = l1.get(i);
            } else if (r > s) {
                return false;
            } else if (++j < l2Size && ++i < l1Size) {
                r = l1.get(i);
                s = l2.get(j);
            }
        }
        return j == l2Size;
    }

    public Itemset del1() {
        Itemset is = new Itemset(this);
        is.items.remove(0);
        return is;
    }
    
    public Itemset delN(Itemset I1, Itemset I2) {
        Itemset ret = new Itemset(I1);
        I1.removeLeadingSubtring(I2.items);
        return ret;
    }

    private void removeLeadingSubtring(List<Integer> r) {
        if (r.isEmpty()) {
            return;
        }
        
        int i = 0;
        while (i < r.size() && !items.isEmpty()) {
            assert r.get(0).equals(items.get(0)) : "removeLeadingSubtring call with r not a leading substring of items";
            items.remove(0);
            i++;
        }
    }
}
