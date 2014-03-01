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
    
    /*
     * http://stackoverflow.com/questions/2927391/whats-the-reason-i-cant-create-generic-array-types-in-java
     * This bugs me
     * 
    public static <T> T[] arrayListtoArray(ArrayList<T> arr) {
        T[] temp = new T[arr.size()];
        int i=0;
        for (T a : arr)
            temp[i++] = 
    }
    */
}
