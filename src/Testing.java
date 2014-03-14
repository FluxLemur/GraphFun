import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;


public class Testing {
    private double alpha = 0.000001;

    @Test
    public void testNodeEdge() {
        Node n1 = new Node(0, 0);
        Node n2 = new Node(3, 4);
        Node n3 = new Node(6, 8);

        //node comparison
        assertEquals(true, n1.compareTo(n2) < 0);
        assertEquals(true, n1.compareTo(n3) < 0);
        assertEquals(true, n2.compareTo(n3) < 0);

        //edge lengths
        Edge e12 = new Edge(n1, n2);
        Edge e13 = new Edge(n1, n3);
        Edge e23 = new Edge(n2, n3);
        assertEquals(5.0, e12.getLength(), alpha);
        assertEquals(10.0, e13.getLength(), alpha);
        assertEquals(5.0, e23.getLength(), alpha);


        //node.getEdges()
        assertEquals(0, n1.getEdges().length);
        assertEquals(0, n2.getEdges().length);
        
        //edge comparison
        assertEquals(true, e13.compareTo(e12) > 0);
        assertEquals(true, e12.compareTo(e23) == 0);

        e12.addNodes();
        Edge[] t_e = {e12};
        assertArrayEquals(t_e, n1.getEdges());
        assertArrayEquals(t_e, n2.getEdges());

        Node[] t_n = {n2};
        assertArrayEquals(t_n, n1.adjNodes());
        t_n = new Node[] {n1};
        assertArrayEquals(t_n, n2.adjNodes());
        
        e12 = new Edge(n1, n2);
        e13 = new Edge(n1, n3);
        Edge e1x = new Edge(n1, new Node(60, 80));
        PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
        pq.add(e12);
        pq.add(e13);
        pq.add(e1x);

        assertEquals(e12, pq.poll());
        assertEquals(e13, pq.poll());
        assertEquals(e1x, pq.poll());

    }

    @Test
    public void unionFindTest() {
        UnionFind<Node> uf = new UnionFind<Node>();
        Node n1 = new Node(1, 0);
        Node n2 = new Node(2, 0);
        Node n3 = new Node(3, 0);
        Node n4 = new Node(4, 0);
        Node n5 = new Node(5, 0);

        Collection<Node> nodes = new HashSet<Node>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n5);

        uf.makeUnionFind(nodes);
        assertEquals(n1, uf.find(n1));
        assertEquals(n2, uf.find(n2));
        assertEquals(n5, uf.find(n5));

        uf.union(n1, n2);
        assertEquals(n2, uf.find(n1));
        assertEquals(n2, uf.find(n2));
        assertEquals(n3, uf.find(n3));
        assertEquals(n4, uf.find(n4));
        assertEquals(n5, uf.find(n5));

        uf.union(n1, n3);
        assertEquals(n2, uf.find(n1));
        assertEquals(n2, uf.find(n2));
        assertEquals(n2, uf.find(n3));
        assertEquals(n4, uf.find(n4));
        assertEquals(n5, uf.find(n5));

        uf.union(n1, n4);
        assertEquals(n2, uf.find(n1));
        assertEquals(n2, uf.find(n2));
        assertEquals(n2, uf.find(n3));
        assertEquals(n2, uf.find(n4));
        assertEquals(n5, uf.find(n5));

    }

}
