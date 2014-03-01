import java.util.*;


public class UnionFind<T> {
    private Map<T, UFNode<T>> node_map;
    
    public UnionFind() {
        node_map = new HashMap<T, UFNode<T>>();
    }
    
    public void makeUnionFind(Collection<T> data) {
        for (T t : data) {
            node_map.put(t, new UFNode<T>(t));
        }
    }
    
    public T find(T in) {
        assert node_map.containsKey(in);
        
        UFNode<T> temp = node_map.get(in);
        //if (temp.parent == null)
        //    return null;
        
        while (temp.parent != null)
            temp = temp.parent;
        
        return temp.data;
    }
    
    /**
     * Never union two of the same 
     * @param a
     * @param b
     */
    public void union(T a, T b) {
        assert node_map.containsKey(a) && node_map.containsKey(b);
        
        UFNode<T> node_a = node_map.get(a);
        UFNode<T> node_b = node_map.get(b);
        if (node_a.compareTo(node_b) > 0) {
            unionHelper(node_b, node_a);
        }
        else
            unionHelper(node_a, node_b);
    }
    
    private void unionHelper(UFNode<T> child, UFNode<T> parent) {
        if (child == parent)
            return;
        UFNode<T> curr = child;
        UFNode<T> temp = curr;
        while (curr != parent && curr != null) {
            temp = curr.parent;
            curr.addParent(parent);
            curr = temp;
        }
    }
    
    public class UFNode<T> implements Comparable {
        T data;
        UFNode<T> parent;
        int size;

        public UFNode(T data) {
            parent = null;
            size = 0;
            this.data = data;
        }
        
        public void addParent(UFNode<T> p) {
            parent = p;
            p.size++;
            size = p.size;
        }
        
        public int compareTo(Object o) {
            UFNode<T> o_node = (UFNode<T>) o;
            int temp = size - o_node.size;
            if (temp != 0)
                return temp;
            else if (parent != null && o_node.parent == null)
                return 1;
            else if (parent == null && o_node.parent != null)
                return -1;
            else if (data instanceof Comparable)
                return ((Comparable) data).compareTo(o_node.data);
            else
                return 1;
        }
        
        public String toString() {
        	return "UFNode[data="+data+"; parent="+parent+"; size="+size+"]";
        }
    }
}
