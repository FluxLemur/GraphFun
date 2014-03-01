/**
 * An Edge connects two Node objects. It has a length and potential weight (not implemented yet)
 * @author leo
 *
 */
public class Edge implements Comparable<Object> {
    private Node from;
    private Node to;
    private double length;
    private double weight;

    public Edge(Node f, Node t) {
        from = f;
        to = t;
        weight = 1;
        length = Node.getDistance(from, to);
    }

    /**
     * Lets the two nodes know that this Edge connects them
     */
    public void addNodes() {
        from.addEdge(this);
        to.addEdge(this);
    }

    public double getWeight() {
        return weight * getLength();
    }
    public double getLength() {
        return length;
    }
    public Node getTo() {
        return to;
    }
    public Node getFrom() {
        return from;
    }

    public int compareTo(Object o) {
        double cmp = getLength() - ((Edge) o).getLength();
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