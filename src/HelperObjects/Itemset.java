package HelperObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Itemset {

    public static final Itemset EMPTY = new Itemset(Collections.emptyList());
    
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
    
    public boolean equals(Itemset r) {
        return equals(items, r.items);
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
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
    
    @Override
    public String toString() {
        return items.toString();
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
            if (r < s) {
                if (++i < l1Size) {
                    r = l1.get(i);
                }
            } else if (r > s) {
                return false;
            } else if (++j < l2Size && ++i < l1Size) {
                r = l1.get(i);
                s = l2.get(j);
            }
        }
        return j == l2Size;
    }

    public static Itemset del1(Itemset is) {
        Itemset ret = new Itemset(is);
        ret.items.remove(0);
        return ret;
    }
    
    public static Itemset delN(Itemset is, Itemset rem) {
        Itemset ret = new Itemset(is);
        ret.removeLeadingSubstring(rem.items);
        return ret;
    }
    
    public static Itemset lss(Itemset I1, Itemset I2) {
        Itemset ret = new Itemset(I1);
        ret.items = getLeadingSubstring(ret.items, I2.items);
        return ret;
    }

    private static List<Integer> getLeadingSubstring(List<Integer> l1, List<Integer> l2) {
        List<Integer> ret = new ArrayList<>();
        
        if (l1.isEmpty() || l2.isEmpty()) {
            return ret;
        }
        
        int i = 0;
        int size = Math.min(l1.size(), l2.size());
        
        while (i < size) {
            int r = l1.get(i);
            int s = l2.get(i);
            if (r == s) {
                ret.add(r);
                i++;
            }
            else {
                break;
            }
        }
        
        return ret;
    }

    private void removeLeadingSubstring(List<Integer> r) {
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
    
    private static boolean equals(List<Integer> l1, List<Integer> l2) {
        int size = l1.size();
        if (size != l2.size()) {
            return false;
        }
        
        for (int i = 0; i < size; i++) {
            if (!l1.get(i).equals(l2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
}
