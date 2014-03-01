
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

import java.awt.Container;

public class GraphDisplay extends JFrame implements ActionListener, MouseListener {
    private int height;
    private int width;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int INIT_POINTS = 10;
    public static final int MAX_RADIUS = 8;

    private JButton gen_points, gen_mst;
    private JTextField points_field;
    private int num_points;
    private JLabel mouse_pos;
    private int p_radius;

    private GraphPanel graph_panel;
    private Graph my_graph;

    public static void main(String[] args) {
        new GraphDisplay();
    }

    public GraphDisplay() {
        super("Graph Display");
        height = HEIGHT;
        width = WIDTH;
        graph_panel = new GraphPanel();
        my_graph = new Graph();
        num_points = INIT_POINTS;
        p_radius = MAX_RADIUS;

        setBounds(0, 0 ,width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create an options/generate nodes/make MST bar on bottom
        java.awt.Container contentPane = getContentPane();
        contentPane.add(graph_panel, BorderLayout.CENTER);
        
        mouse_pos = new JLabel("(0,0)", JLabel.NORTH_EAST);
        graph_panel.add(mouse_pos, BorderLayout.EAST);
        graph_panel.addMouseMotionListener(new MouseMotionListener() {	
			public void mouseMoved(MouseEvent e) {
				mouse_pos.setText("("+e.getX()+","+e.getY()+")");
			}
			public void mouseDragged(MouseEvent e) {}
		});


        // Button panel
        JPanel bpanel = new JPanel();
        bpanel.setLayout(new BoxLayout(bpanel, BoxLayout.X_AXIS));

        points_field = new JTextField(num_points+"");
        points_field.addActionListener(this);
        bpanel.add(points_field);

        gen_points = new JButton("Generate " + num_points + " random nodes");
        gen_points.addActionListener(this);
        bpanel.add(gen_points);
        gen_mst = new JButton("Generate minimum spanning tree");
        gen_mst.addActionListener(this);
        bpanel.add(gen_mst);

        contentPane.add(bpanel, BorderLayout.PAGE_END);
        setVisible(true);

    }

    // Button Listener
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(gen_points)) {
            int w = graph_panel.getWidth();
            int h = graph_panel.getHeight();
        	p_radius = Utils.scaledPointSize(num_points, w, h);
            my_graph.reset();
            my_graph.addNodes(Utils.genPoints(num_points, w, h), w, h);
            repaint();
        }
        else if (e.getSource().equals(gen_mst)) {
            //my_graph.genEdges2();
            my_graph.genMST2();
            repaint();
        }
        else if (e.getSource().equals(points_field)) {
            String new_points = points_field.getText();
            try {
                int num = Integer.parseInt(new_points);
                gen_points.setText("Generate " + num + " random nodes");
                num_points = num;
            } catch (Exception exc) {
                System.out.println("Please enter a positive integer!");
            }
        }
    }


    private class GraphPanel extends JPanel {
        public GraphPanel() {
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public Dimension getPreferredSize() {
            return new Dimension(WIDTH,HEIGHT);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw randomly generated nodes
            for (Node p : my_graph.getNodes())
                g.fillOval(p.getX()-p_radius/2, p.getY()-p_radius/2, p_radius, p_radius);
            for (Edge e : my_graph.getEdges()) {
                if (e != null)
                    g.drawLine(e.getFrom().getX(), e.getFrom().getY(), e.getTo().getX(), e.getTo().getY());
            }

        }
    }



	public void mouseClicked(MouseEvent e) {
		//if (e.getX())
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}