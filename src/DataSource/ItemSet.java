package DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ItemSet implements Iterable<Integer> {

    public static final ItemSet EMPTY = new ItemSet(Collections.emptyList());

    private List<Integer> items;

    public ItemSet() {
        items = new ArrayList<>();
    }

    public ItemSet(ItemSet is) {
        items = new ArrayList<>(is.items);
    }

    public ItemSet(Collection<Integer> items) {
        this.items = new ArrayList<>(items);
        Collections.sort(this.items);
    }

    public ItemSet(int i) {
        this();
        items.add(i);
    }

    public List<Integer> getItems() {
        return items;
    }

    public Integer get(int idx) {
        return items.get(idx);
    }

    public ItemSet append(int i) {
        int idx = getInsertionIndex(items, i);
        if (idx >= 0) {
            System.out.println("Cannot have duplicates in ItemSet");
        } else {
            items.add(-idx - 1, i);
        }
        return this;
    }

    // By Dr. Srinivas Sampalli
    private int getInsertionIndex(List<Integer> elements, int i) {
        if (elements.size() == 0) {
            return -1;
        }

        int lo = 0, hi = elements.size() - 1, mid = 0, c = 0;

        while (lo <= hi) {
            mid = (lo + hi) / 2;
            c = i - elements.get(mid);
            if (c == 0) {
                return mid;
            }
            if (c < 0) {
                hi = mid - 1;
            }
            if (c > 0) {
                lo = mid + 1;
            }
        }

        if (c < 0)
            return (-mid - 1);
        else
            return (-mid - 2);
    }

    public ItemSet append(ItemSet is) {
        is.items.forEach(this::append);
        return this;
    }

    public ItemSet union(ItemSet is) {
        ItemSet ret = new ItemSet();
        return ret.append(this).append(is);
    }

    public int size() {
        return items.size();
    }

    public Integer remove(int n) {
        return items.remove(n);
    }

    public boolean equals(ItemSet r) {
        return equals(items, r.items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean greaterThan(ItemSet r) {
        return compare(items, r.items) > 0;
    }

    public static int compare(List<Integer> l1, List<Integer> l2) {
        for (int i = 0; i < l1.size() && i < l2.size(); i++) {
            int r = l1.get(i);
            int s = l2.get(i);

            if (r > s) {
                return 1;
            } else if (r < s) {
                return -1;
            }
        }

        return l1.size() - l2.size();
    }

    public boolean contains(ItemSet r) {
        return contains(items, r.items);
    }

    public boolean containedBy(ItemSet r) {
        return contains(r.items, items);
    }

    @Override
    public String toString() {
        return items.toString();
    }

    private static boolean contains(List<Integer> l1, List<Integer> l2) {
        if (l1.size() <= l2.size()) {
            return false;
        }

        int i = 0;
        int j = 0;

        while (i < l1.size() && j < l2.size()) {
            if (l1.get(i) > l2.get(j)) {
                return false;
            } else if (l1.get(i) < l2.get(j)) {
                i++;
            } else {
                i++;
                j++;
            }
        }

        return j >= l2.size();
    }

    public static ItemSet del1(ItemSet is) {
        ItemSet ret = new ItemSet(is);
        ret.items.remove(0);
        return ret;
    }

    public static ItemSet delN(ItemSet is, ItemSet rem) {
        ItemSet ret = new ItemSet(is);
        ret.removeLeadingSubstring(rem.items);
        return ret;
    }

    public static ItemSet delN(ItemSet is, int j) {
        ItemSet ret = new ItemSet(is);
        while (j-- > 0) {
            ret.items.remove(0);
        }
        return ret;
    }

    public static ItemSet lss(ItemSet I1, ItemSet I2) {
        ItemSet ret = new ItemSet(I1);
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
            } else {
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

    public static ItemSet doubleton(int i1, int i2) {
        ItemSet ret = new ItemSet();
        ret.append(i1);
        ret.append(i2);
        return ret;
    }

    @Override
    public Iterator<Integer> iterator() {
        return items.iterator();
    }
}
