package Book;


/**
 * Keeps track of current location in the grid and which node it came from
 * start came from null
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Node implements Comparable<Node>
{
    final MazeLocation state;
    Node parent;
    double cost, heuristic;
    
    public Node(MazeLocation state, Node parent) {
        this.state = state;
        this.parent = parent;
    }
    //for a* only
    public Node(MazeLocation state, Node parent, double cost, double heuristic) {
        this.state = state;
        this.parent = parent;
        this.cost = cost;
        this.heuristic = heuristic;
    }
    //needed for priorityqueue
    public int compareTo(Node n) {
        double mine = cost+heuristic;
        double theirs = n.cost + n.heuristic;
        if(mine<theirs) return -1;
        if(mine>theirs) return 1;
        return 0;
    }
    
}
