import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/** Represents nodes and edges in a graph.
 *  Also provides functions to generate an MST from the nodes. */
public class Graph {
  private Edge[] edges;
  private ArrayList<Node> nodes;
  private HashMap<String, Node> node_map;	// A mapping from (i, j) locations to Nodes
  private int widthBound;
  private int heightBound;

  public Graph() {
    reset();
  }

  public Edge[] getEdges() {
    return edges;
  }

  private String coordString(int x, int y) {
    return x+" "+y;
  }

  /** Returns the node at coords (x, y), null if none */
  public Node nodeAt(int x, int y) {
    String s = coordString(x, y);
    if (node_map.containsKey(s)) {
      Node n = node_map.get(s);
      if (n.x == x && n.y == y)
        return n;
    }
    return null;
  }

  /** Add an array of nodes within bounds x: [0, width], y : [0, height] */
  public void addNodes(Node[] nodes, int width, int height) {
    //this.nodes = new ArrayList<Node>(Arrays.asList(nodes));
    for (Node n : nodes) {
      this.nodes.add(n);
      node_map.put(coordString(n.x, n.y), n);
    }
    widthBound = width;
    heightBound = height;
  }

  public Node[] getNodes() {
    Node[] temp = new Node[nodes.size()];
    int i = 0;
    for (Node n : nodes)
      temp[i++] = n;
    return temp;
  }

  public void reset() {
    edges = new Edge[0];
    nodes = new ArrayList<Node>();
    node_map = new HashMap<String, Node>();
    widthBound = 0;
    heightBound = 0;
  }

  /** Generate all possible Edges in the Graph by all combinations */
  public void genComboEdges() {
    int size = nodes.size();
    int len = size * (size - 1) / 2;
    Edge[] all = new Edge[len];
    int c = 0;
    for (int i = 0; i < size; i++) {
      for (int j = i+1; j<size; j++) {
        all[c++] = new Edge(nodes.get(i), nodes.get(j));
      }
    }
    edges = all;
  }

  private void clearNodeEdges() {
    for (Node n : nodes) {
      n.clearEdges();
    }
  }

  /** Determines whether all combinations, or BFS on points should be used to generate Edges */
  private boolean getCombos() {
    // If points are sparse, use combinations
    return widthBound / (double) nodes.size() > nodes.size() / (double) heightBound;
  }

  /** Generate all possible Edges in the Graph by local BFS on points */
  public void genBFSEdges() {
    ArrayList<Edge> tempEdges = new ArrayList<Edge>();
    Collection<Node> pairedNodes = new HashSet<Node>();
    int radius = 2 * (int) Math.sqrt(widthBound * heightBound / nodes.size());

    // Find all local edges by BFS
    for (Node n : nodes) {
      for (int x = n.x - radius; x < n.x + radius; x++) {
        for (int y = n.y - radius; y < n.y + radius; y++) {
          Node temp = nodeAt(x, y);
          if (temp != null && !temp.equals(n)) {
            tempEdges.add(new Edge(n, temp));
            pairedNodes.add(n);
          }
        }
      }
    }

    // All unpaired nodes make edges to all other nodes
    for (int i = 0; i < nodes.size(); i++) {
      Node n = nodes.get(i);
      if (!pairedNodes.contains(n)) {
        for (int j = i+1; j < nodes.size(); j++){
          tempEdges.add(new Edge(n, nodes.get(j)));
        }
      }
    }


    // ArrayList -> Array
    edges = tempEdges.toArray(edges);
  }

  // Checks if nodes a and b are connected using BFS
  public boolean nodesConnected(Node a, Node b) {
    return nodeConnectionHelper(null, a, b) != null;
  }

  private boolean nodesConnectedHelper(Node parent, Node from, Node to) {
    if (from == to)
      return true;
    for (Node n : from.adjNodes()) {
      if (n != parent && nodesConnectedHelper(from, n, to))
        return true;
    }
    return false;
  }

  /** Returns a list of Edges if there is a connection between from and to,
   * otherwise null */
  public Edge[] nodeConnectionHelper(Node parent, Node from, Node to) {
    if (from == to)
      return new Edge[0];

    for (Node n : from.adjNodes()) {
      if (n != parent) {
        Edge[] temp = nodeConnectionHelper(from, n, to);
        if (temp != null) {
          Edge[] ret = new Edge[temp.length + 1];
          ret[0] = from.edgeTo(n);
          for (int i = 0; i<temp.length; i++) {
            ret[i+1] = temp[i];
          }
          return ret;
        }
      }
    }
    return null;
  }

  /** Generates an MST using Kruskal's and BFS for cycle-checks
   */
  public void genMSTSlow() {
    clearNodeEdges();
    final long startTime = System.currentTimeMillis();
    long totalTime = 0;
    long tempTime = 0;
    System.out.println("Connecting "+nodes.size()+" nodes sub-optimally");

    genComboEdges();
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Generated "+edges.length+" edges: " + tempTime/1000.0 + " sec");

    Arrays.sort(edges);
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Sorted edges: " + tempTime/1000.0 + " sec");

    Edge[] mst = new Edge[nodes.size()];
    int i = 0;

    for (Edge e : edges) {
      if (!nodesConnected(e.from, e.to)) {    // e does not create a cycle
        mst[i++] = e;
        e.addNodes();
      }
      if (i == mst.length) {
        break;
      }
    }
    edges = mst;
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Finished MST: " + tempTime/1000.0 + " sec");
    System.out.println("Total time elapsed: " + totalTime/1000.0 + " sec\n");
  }

  /** Generates an MST using Kruskal's and Union-Find for cycle-checking
   */
  public void genMSTFast() {
    final long startTime = System.currentTimeMillis();
    long totalTime = 0;
    long tempTime = 0;
    System.out.println("Connecting "+nodes.size()+" nodes optimally");

    // Generate edges for Graph
    if (getCombos()) {
      System.out.println("Using all edge combinations...");
      genComboEdges();
    } else {
      System.out.println("Using BFS to make edges...");
      genBFSEdges();
    }
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Generated "+edges.length+" edges: " + tempTime/1000.0 + " sec");

    // Generate the edge PriorityQueue
    PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
    for (Edge e : edges)
      pq.add(e);
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Generated edge priority queue: " + tempTime/1000.0 + " sec");

    // Generate MST
    UnionFind<Node> uf = new UnionFind<Node>();
    uf.makeUnionFind(nodes);

    Edge[] mst = new Edge[nodes.size()-1];
    int i = 0;

    Edge e;
    while (i < mst.length) {
      e = pq.poll();
      if (e == null)
        break;
      if (uf.find(e.from) != uf.find(e.to)) {    // e does not create a cycle
        mst[i++] = e;
        e.addNodes();
        uf.union(e.from, e.to);
      }
    }
    edges = mst;
    tempTime = System.currentTimeMillis() - startTime - totalTime;
    totalTime += tempTime;
    System.out.println("Finished MST: " + tempTime/1000.0 + " sec");
    System.out.println("Total time elapsed: " + totalTime/1000.0 + " sec\n");
  }
}
