import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Graph {
    private Edge[] edges;
    private ArrayList<Node> nodes;
    private HashMap<String, Node> node_map;	// A mapping from (i, j) locations to Nodes
    private int widthBound;
    private int heightBound;

    public Graph() {
        reset();
    }

    /** 
     * Generate an array of edges that for an list of nodes
     * @param nodes
     */
    public Edge[] getEdges() {
        return edges;
    }
    
    private String coordString(int x, int y) {
    	return x+""+y;
    }

    /** Add an array of nodes within bounds x: [0, width], y : [0, height] */
    public void addNodes(Node[] nodes, int width, int height) {
        //this.nodes = new ArrayList<Node>(Arrays.asList(nodes));
        for (Node n : nodes) {
            this.nodes.add(n);
            node_map.put(coordString(n.getX(), n.getY()), n);
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
    public void genEdges() {
        int size = nodes.size();
        int len = size * (size - 1) / 2;
        Edge[] all = new Edge[len];
        int c = 0;
        for (int i = 0; i < size; i++)
            for (int j = i+1; j<size; j++)
                all[c++] = new Edge(nodes.get(i), nodes.get(j));
        edges = all;
    }

    /** Determines whether all combinations, or BFS on points should be used to generate Edges */
    private boolean getCombos() {
        // If points are sparse, use combinations
        return widthBound * heightBound > nodes.size() * nodes.size();
    }

    /** Generate all possible Edges in the Graph by local BFS on points */
    public void genEdges2() {
    	ArrayList<Edge> tempEdges = new ArrayList<Edge>();
    	Collection<Node> pairedNodes = new HashSet<Node>();
    	int radius = 2 * (int) Math.sqrt(widthBound * heightBound / nodes.size());
    	
    	// Find all local edges by BFS
    	for (Node n : nodes) {
    		for (int x = n.getX() - radius; x < n.getX() + radius; x++) {
    			for (int y = n.getY() - radius; y < n.getY() + radius; y++) {
    				String s = coordString(x, y);
    				if (node_map.containsKey(s)) {
    					Node tmp = node_map.get(s);
    					if (tmp.getX() == x && tmp.getY() == y && !tmp.equals(n)) {
    						tempEdges.add(new Edge(n, node_map.get(s)));
    		    			pairedNodes.add(n);
    					}
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
        return nodesConnectedHelper(null, a, b);
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

    /**
     * Generates an MST using Kruskal's and BFS for cycle-checks
     * @param edges List of Edge objects
     * @return Edge list representing MST
     */
    public void genMST() {
        final long startTime = System.currentTimeMillis();
        genEdges();
        System.out.println("Finished ALL edges : " + (System.currentTimeMillis() - startTime)/1000 + " sec");
        Arrays.sort(edges);
        System.out.println("Finished sorting edges: " + (System.currentTimeMillis() - startTime)/1000 + " sec");
        Edge[] mst = new Edge[nodes.size()];
        int i = 0;

        for (Edge e : edges) {
            if (!nodesConnected(e.getFrom(), e.getTo())) {    // e does not create a cycle
                mst[i++] = e;
                e.addNodes();
            }
            if (i == mst.length) {
                break;
            }
        }
        edges = mst;
        System.out.println("Finished MST " + (System.currentTimeMillis() - startTime)/1000 + " sec");
    }

    /**
     * Generates an MST using Kruskal's and Union-Find for cycle-checking
     * @param edges List of Edge objects
     * @return Edge list representing MST
     */
    public void genMST2() {
        final long startTime = System.currentTimeMillis();
        long totalTime = 0;
        long tempTime = 0;
        System.out.println("Connecting "+nodes.size()+" nodes optimally");

        // Generate edges for Graph
        if (getCombos()) {
            System.out.println("Using all edge combinations...");
            genEdges();
        } else {
            System.out.println("Using BFS to make edges...");
            genEdges2();
        }
        tempTime = System.currentTimeMillis() - startTime;
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
            if (uf.find(e.getFrom()) != uf.find(e.getTo())) {    // e does not create a cycle
                mst[i++] = e;
                e.addNodes();
                uf.union(e.getFrom(), e.getTo());
            }
        }
        edges = mst;
        tempTime = System.currentTimeMillis() - startTime - totalTime;
        totalTime += tempTime;
        System.out.println("Finished MST: " + tempTime/1000.0 + " sec");
        System.out.println("Total time elapsed: " + totalTime/1000.0 + " sec\n");
    }
}
