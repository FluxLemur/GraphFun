import java.util.ArrayList;

/**
 * A Node is a point in a Cartesian Plane with integer coordinates
 * and has edges connecting it, comprising a Graph.
 */
public class Node implements Comparable<Object> {
  public final int x;
  public final int y;
  private ArrayList<Edge> edges;

  /**
   * Constructs Node object at coordinates (x, y)
   * @param x
   * @param y
   */
  public Node(int x, int y) {
    this.x = x;
    this.y = y;
    edges = new ArrayList<Edge>();
  }

  public static double getDistance(Node a, Node b) {
    double d_x = a.x - b.x;
    d_x *= d_x;
    double d_y = a.y - b.y;
    d_y *= d_y;

    return Math.sqrt(d_x + d_y);
  }

  public void clearEdges() {
    edges.clear();
  }

  public void addEdge(Edge e) {
    edges.add(e);
  }

  public Edge[] getEdges() {
    Edge[] temp = new Edge[edges.size()];
    int i = 0;
    for (Edge e : edges) {
      temp[i++] = e;
    }
    return temp;
  }

  public Node[] adjNodes() {
    Node[] adjs = new Node[edges.size()];
    int i = 0;
    for (Edge e : edges) {
      adjs[i++] = e.getOther(this);
    }
    return adjs;
  }

  @Override
  public String toString() {
    return "Node("+x+", "+y+")";
  }

  /**
   * Get Edge object from this Node to other, null if none
   * @param other
   * @return
   */
  public Edge edgeTo(Node other) {
    for (Edge e : edges) {
      if (e.hasNode(other))
        return e;
    }
    return null;
  }

  /*
     @Override
     public boolean equals(Object o) {
     if (! (o instanceof Node))
     return false;
     Node n = (Node) o;
     return super.equals(n) && n.x == x && n.y == y && n.edges.equals(edges);
     }
     */

  /*
     @Override
     public int hashCode() {
     int prime = 31;
     int result = 1;
     result += prime * super.hashCode();
     result += prime * Math.abs(x);
     result += prime * Math.abs(y);
     result += prime * edges.hashCode();
     return result;
     }
     */

  public int compareTo(Object o) {
    assert o instanceof Node;
    Node temp = (Node) o;
    String s1 = x+""+y;
    String s2 = temp.x+""+temp.y;

    return s1.compareTo(s2);
  }
}
