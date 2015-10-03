/**
 * An Edge connects two Node objects.
 */
public class Edge implements Comparable<Object> {
    public final Node from;
    public final Node to;
    public final double length;
    private double weight;

    public Edge(Node f, Node t) {
        from = f;
        to = t;
        weight = 1;
        length = Node.getDistance(from, to);
    }

    /**
     * Both Nodes point to this edge.
     */
    public void addNodes() {
        from.addEdge(this);
        to.addEdge(this);
    }

    /** Whether this edge contains Node n*/
    public boolean hasNode(Node n) {
    	return n == from || n == to;
    }

    public double getWeightedLength() {
        return weight * length;
    }

    public int compareTo(Object o) {
        double cmp = length - ((Edge) o).length;
        if (Math.abs(cmp) < 0.000000000001)
            return 0;
        if (cmp > 0)
            return 1;
        return -1;
    }

    /**
     * Returns the Node of this edge that != in
     * null if != from or to
     * @param in
     * @return
     */
    public Node getOther(Node in) {
        if (in == from)
            return to;
        if (in == to)
            return from;
        return null;
    }

    @Override
    public String toString() {
        return "["+from+", "+to+"]";
    }
}
