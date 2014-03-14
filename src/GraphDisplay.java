
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

@SuppressWarnings("serial")
public class GraphDisplay extends JFrame implements ActionListener, MouseListener {
    private int height;
    private int width;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int INIT_POINTS = 10;
    public static final int MAX_RADIUS = 8;
    public static final Color POINT_COLOR = Color.black;

    private JButton genPoints, genMst;
    private JTextField pointsField;
    private int numPoints;
    private JLabel mousePos;
    private int pRadius;
    private Point mousePress;
    private Node fromNode;
    private Node toNode;

    private GraphPanel graphPanel;
    private Graph myGraph;

    public static void main(String[] args) {
        new GraphDisplay();
    }

    public GraphDisplay() {
        super("Graph Display");
        height = HEIGHT;
        width = WIDTH;
        graphPanel = new GraphPanel();
        myGraph = new Graph();
        numPoints = INIT_POINTS;
        pRadius = MAX_RADIUS;
        mousePress = null;

        setBounds(0, 0 ,width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create an options/generate nodes/make MST bar on bottom
        java.awt.Container contentPane = getContentPane();
        contentPane.add(graphPanel, BorderLayout.CENTER);
        
        mousePos = new JLabel("(0,0)", JLabel.NORTH_EAST);
        graphPanel.add(mousePos, BorderLayout.EAST);
        graphPanel.addMouseListener(this);
        graphPanel.addMouseMotionListener(new MouseMotionListener() {	
			public void mouseMoved(MouseEvent e) {
				mousePos.setText("("+e.getX()+","+e.getY()+")");
			}
			public void mouseDragged(MouseEvent e) {
				if (mousePress != null) {
					Graphics2D g = (Graphics2D) graphPanel.getGraphics();
					int x = Math.min(mousePress.x, e.getX());
					int y = Math.min(mousePress.y, e.getY());
					int w = Math.abs(e.getX() - mousePress.x);
					int h = Math.abs(e.getY() - mousePress.y);
					graphPanel.zoomBox = new Rectangle(x, y, w, h);
					graphPanel.repaint();
				}
			}
		});


        // Button panel
        JPanel bpanel = new JPanel();
        bpanel.setLayout(new BoxLayout(bpanel, BoxLayout.X_AXIS));

        pointsField = new JTextField(numPoints+"");
        pointsField.addActionListener(this);
        bpanel.add(pointsField);

        genPoints = new JButton("Generate " + numPoints + " random nodes");
        genPoints.addActionListener(this);
        bpanel.add(genPoints);
        genMst = new JButton("Generate minimum spanning tree");
        genMst.addActionListener(this);
        bpanel.add(genMst);

        contentPane.add(bpanel, BorderLayout.PAGE_END);
        setVisible(true);

    }

    // Button Listener
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(genPoints)) {
            int w = graphPanel.getWidth();
            int h = graphPanel.getHeight();
        	pRadius = Utils.scaledPointSize(numPoints, w, h);
            myGraph.reset();
			resetPath();
            myGraph.addNodes(Utils.genPoints(numPoints, w, h), w, h);
            repaint();
        }
        else if (e.getSource().equals(genMst)) {
            //myGraph.genEdges2();
            myGraph.genMST2();
            repaint();
        }
        else if (e.getSource().equals(pointsField)) {
            String new_points = pointsField.getText();
            try {
                int num = Integer.parseInt(new_points);
                genPoints.setText("Generate " + num + " random nodes");
                numPoints = num;
            } catch (Exception exc) {
                System.out.println("Please enter a positive integer!");
            }
        }
    }


    private class GraphPanel extends JPanel {
    	public Rectangle zoomBox;
    	public Edge[] path;
        public GraphPanel() {
            setBorder(BorderFactory.createLineBorder(Color.black));
            zoomBox = null;
            path = null;
        }

        public Dimension getPreferredSize() {
            return new Dimension(WIDTH,HEIGHT);
        }

        public void paintComponent(Graphics g) {
        	Graphics2D g2 = (Graphics2D) g;
            super.paintComponent(g2);

            // Draw randomly generated nodes
            for (Node p : myGraph.getNodes())
                drawPoint(g2, p.getX(), p.getY());
            
            //Draw selected Nodes
			g2.setColor(Color.blue);
            if (fromNode != null)
                graphPanel.drawPoint(g2 , fromNode.getX(), fromNode.getY());
            if (toNode != null)
                graphPanel.drawPoint(g2 , toNode.getX(), toNode.getY());
            
            g2.setColor(POINT_COLOR);
            
            g2.setStroke(new BasicStroke(2));
            //g2.setColor(Color.red);
            drawEdges(g2, myGraph.getEdges());
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.blue);
            drawEdges(g2, path);
            
            g2.setColor(POINT_COLOR);
            // Draw zoom box
            if (zoomBox != null)
            	g2.draw(zoomBox);
        }
        
        public void drawPoint(Graphics2D g, int x, int y) {
        	g.fillOval(x-pRadius/2, y-pRadius/2, pRadius, pRadius);
        }
        
        public void drawEdges(Graphics2D g, Edge[] edges) {
        	if (edges == null)
        		return;
        	for (Edge e : edges) {
                if (e != null)
                    g.drawLine(e.getFrom().getX(), e.getFrom().getY(), e.getTo().getX(), e.getTo().getY());
            }
        }
        
        public void pathInfo() {
        	if (path == null || toNode == null || fromNode == null)
        		return;
        	double length = 0;
            for (Edge e : path) {
            	length += e.getLength();
            }
            System.out.println("Direct path distance: "+Node.getDistance(fromNode, toNode));
            System.out.println("Distance on MST: " +length+"\n");
        }
    }

    public void resetPath() {
    	toNode = null;
		fromNode = null;
		graphPanel.path = null;
    }

    // Clicking on two points highlights them
    // Right click clears stored points pair
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			resetPath();
			repaint();
			return;
		}
		for (int i = e.getX() - pRadius; i<e.getX()+pRadius; i++) {
			for (int j = e.getY() -pRadius; j<e.getY()+pRadius; j++) {
				Node temp = myGraph.nodeAt(i, j);
				if (temp != null) {
					if (fromNode == null) {
						fromNode = temp;
					}
					else if (toNode == null) {
						toNode= temp;
						if (myGraph.getEdges().length > 0) { // There is an MST
							graphPanel.path = myGraph.nodeConnectionHelper(null, fromNode, toNode);
							graphPanel.repaint();
							//graphPanel.pathInfo();	// NOTE: Turn on path info here!
						}
					}
					else {	// start new pair
						fromNode = temp;
						toNode = null;
						graphPanel.path = null;
					}
					repaint();
					return;
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		mousePress = e.getPoint();
	}
	public void mouseReleased(MouseEvent e) {
		if (mousePress != null && !(e.getPoint().equals(mousePress))) {
			//System.out.println("Transforming");
			Graphics2D g = (Graphics2D) graphPanel.getGraphics();
			AffineTransform tr = g.getTransform();
			g.scale(e.getX()-mousePress.x, e.getY()-mousePress.y);
			//g.setTransform(tr);
			//graphPanel.repaint();
		}
		graphPanel.zoomBox = null;
		graphPanel.repaint();
	}
}