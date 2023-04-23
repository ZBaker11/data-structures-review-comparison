package csc365;

import java.util.ArrayList;

class HT {
    static final class Node {
        Object key;
        Node next;
        int count;
        // Object value;
        Node(Object k, Node n, int c) { key = k; next = n; count = c; }

        public String toString() { return key.toString(); }
    }

    Node[] table = new Node[8];
    int size = 0;

    boolean contains(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                return true;
        }
        return false;
    }

    public void add(Object key) {
	int h = key.hashCode();
	int i = h & (table.length - 1);
	for (Node e = table[i]; e != null; e = e.next) {
	    if (key.equals(e.key)) {
            e.count++; 
            return;
        }
	}

	table[i] = new Node(key, table[i], 1); // key did not exist
	size++;
	if ((float) size / table.length >= 0.75f)
	    resize();
    }

    public int get(Object key) { // returns multiplicity of key
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)) {
                return e.count;
            }
        }
        return 0;
    }

    public ArrayList<Object> keySet() {
        ArrayList<Object> keySet = new ArrayList<Object>();
        for (int i = 0; i < table.length; i++) {
            for (Node e = table[i]; e != null; e = e.next) {
                keySet.add(e.key);
            }
        }
        return keySet;
    }


    void resize() { // currently broken?
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; i++) {
            for (Node e = oldTable[i]; e != null; e = e.next) {
                int h = e.key.hashCode();
                int j = h & (newCapacity - 1);
                newTable[j] = new Node(e.key, newTable[j], e.count);
            }
        }
        table = newTable;
    }


    void remove(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        Node e = table[i], p = null;
        while (e != null) {
            if (key.equals(e.key)) {
                if (p == null)
                    table[i] = e.next;
                else
                    p.next = e.next;
                break;
            }
            p = e;
            e = e.next;
        }
    }
}