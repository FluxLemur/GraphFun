import java.util.Random;


public class Utils {
    /**
     * Generates list of nodes randomly picked between x: [0, width] and y: [0, height]
     * @param num Number of nodes to generate
     */
    public static Node[] genPoints(int num, int width, int height) {
        Random gen = new Random();
        Node[] ps = new Node[num];
        for (int i=0; i<num; i++) {
            ps[i] = new Node(gen.nextInt(width), gen.nextInt(height));
        }

        return ps;
    }

    public static int scaledPointSize(int num_points, int width, int height) {
    	return (int) Math.min(GraphDisplay.MAX_RADIUS, Math.sqrt(0.5*width*height/num_points));
    }
}
