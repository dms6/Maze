package Book;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
//depth-first-search
import java.util.Stack;
import java.util.HashSet;
//breadth-first-search
import java.util.LinkedList;
import java.util.Queue;
//a*
import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;
/**
 * Randomly generates maze, solves it using dfs, bfs, and a*
 * 
 * @author Dillon Shelton
 * @version 4/21/22
 */
public class Maze
{
    public enum Cell{
        BLOCKED("X"), 
        START("S"),
        GOAL("G"), 
        EMPTY(" "),
        PATH("*"),
        SEARCHED(""+'\u00B7');
        private final String line;
        private Cell(String s){
            line = s;
        }
        public String toString(){
            return line;
        }
    }
    
    private Cell[][] grid;
    private final int rows, columns;
    private final MazeLocation start, goal;
    public int searchLength = 0, pathLength = 0;
  
    public Maze(int rows, int columns, MazeLocation start, MazeLocation goal, double sparseness){
        this.rows = rows;
        this.columns = columns;
        this.start = start;
        this.goal = goal;
        grid = new Cell[rows][columns];
        for(Cell[] row : grid){
            Arrays.fill(row, Cell.EMPTY);
        }
        randomlyFill(sparseness);
        grid[start.row][start.column] = Cell.START;
        grid[goal.row][goal.column] = Cell.GOAL;
    }
    //Default maze
    public Maze(){
        this(10,10, new MazeLocation(0,0), new MazeLocation(9,9), 0.15);
    }
    
    public void randomlyFill(double sparseness){
        //if math.random is less than sparseness, fill it with an X
        for(int i = 0; i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){
                if(Math.random() < sparseness){
                    grid[i][j] = Cell.BLOCKED;
                }
            }
        }
    }
    
    public boolean goalTest(MazeLocation m){
        return goal.equals(m);
    }
    
    public List<MazeLocation> successors(MazeLocation m){
        List<MazeLocation> list= new ArrayList<>();
        //this prioritizes down right. If you switch the goal to 
        //the top-left and start to bottom-right for dfs,
        //its pretty disastrous. A* does much better because
        //it uses cost to decide which direction to go.
        //(I could use if statements to change order depending on 
        //location of goal relative to start, but thats not the point of this)
        //left
        if(m.column-1>=0 && grid[m.row][m.column-1] != Cell.BLOCKED){
            list.add(new MazeLocation(m.row, m.column-1));
        }
        //up
        if(m.row-1>=0 && grid[m.row-1][m.column] != Cell.BLOCKED){
            list.add(new MazeLocation(m.row-1, m.column));
        }
        
        //down
        if(m.row+1<rows && grid[m.row+1][m.column] != Cell.BLOCKED){
            list.add(new MazeLocation(m.row+1, m.column));
        }
        //right
        if(m.column+1<columns && grid[m.row][m.column+1] != Cell.BLOCKED){
            list.add(new MazeLocation(m.row, m.column+1));
        }
        
        return list;
    }
    
    public Node dfs(){
        Stack<Node> frontier = new Stack<>();
        frontier.push(new Node(start, null));
        Set<MazeLocation> explored = new HashSet<>();
        explored.add(start);
        while(!frontier.isEmpty()){
            Node currentNode = frontier.pop();
            searchLength++; 
            grid[currentNode.state.row][currentNode.state.column] = Cell.SEARCHED;
            
            MazeLocation currentState = currentNode.state;
            if(goalTest(currentState)) return currentNode;
            for(MazeLocation child : successors(currentState)){
                if(explored.contains(child)) continue;
                explored.add(child);
                frontier.push(new Node(child, currentNode));
            }
        }
        return null;
    }
    
    public Node bfs(){
        Queue<Node> frontier = new LinkedList<>();
        frontier.offer(new Node(start, null));
        Set<MazeLocation> explored = new HashSet<>();
        explored.add(start);
        while(!frontier.isEmpty()){
            Node currentNode = frontier.poll();
            searchLength++;
            grid[currentNode.state.row][currentNode.state.column] = Cell.SEARCHED;
            
            MazeLocation currentState = currentNode.state;
            if(goalTest(currentState)) return currentNode;
            for(MazeLocation child : successors(currentState)){
                if(explored.contains(child)) continue;
                explored.add(child);
                frontier.offer(new Node(child, currentNode));
            }
        }
        return null;
    }
    //Heuristic
    public int manhattanDistance(MazeLocation m){
        int xDist = Math.abs(m.column - goal.column);
        int yDist = Math.abs(m.row - goal.row);
        return (xDist + yDist);
    }
    public Node astar(){
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        frontier.offer(new Node(start, null, 1, manhattanDistance(start)));
        Map<MazeLocation, Double> explored = new HashMap<>();
        explored.put(start, 0.0);
        while(!frontier.isEmpty()){
            Node currentNode = frontier.poll();
            searchLength++;
            grid[currentNode.state.row][currentNode.state.column] = Cell.SEARCHED;
            MazeLocation currentState = currentNode.state;
            if(goalTest(currentState)) return currentNode;
            //if it doesn't contain it yet or takes longer to get there, set new one. 
            for(MazeLocation child : successors(currentState)){
                if(!explored.containsKey(child)||explored.get(child)>currentNode.cost+1){
                    explored.put(child,currentNode.cost + 1);
                    frontier.offer(new Node(child, currentNode, currentNode.cost+1, manhattanDistance(child)));
                }
            }
        }
        return null;
    }
    
    
    public ArrayList<MazeLocation> nodeToPath(Node n){
        ArrayList<MazeLocation> list = new ArrayList<MazeLocation>();
        while(n!=null){
            list.add(n.state);
            pathLength++;
            grid[n.state.row][n.state.column] = Cell.PATH;
            n=n.parent;
        }
        return list;
    }
    public void mark(ArrayList<MazeLocation> list){
        for(MazeLocation m : list){
            grid[m.row][m.column] = Cell.PATH;
        }
    }
    //print maze
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------\n");
        for(Cell[] arr : grid){
            for(Cell cell : arr){
                sb.append(cell.toString());
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public void reset(){
        for(int i = 0; i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){
                if(grid[i][j] == Cell.PATH || grid[i][j] == Cell.SEARCHED){
                    grid[i][j] = Cell.EMPTY;
                }
            }
        }
        searchLength = 0;
        pathLength = 0;
        grid[start.row][start.column] = Cell.START;
        grid[goal.row][goal.column] = Cell.GOAL;
    }
    public static void main(String[] args){
        Node node;
        ArrayList<MazeLocation> list = new ArrayList<>();
        
        Maze m = new Maze(10,20,new MazeLocation(0,0), new MazeLocation(9,19), 0.25);
        System.out.println(m);
        
        node = m.dfs();
        if(node==null) System.out.println("No path available!");
        else{
       
        list = m.nodeToPath(node);
        m.mark(list);
        System.out.println(m);
        System.out.println("DFS: Less searches, not optimal\nPath Length: " + m.pathLength + "\nSearch Length: " + m.searchLength );
        m.reset();
        
        node = m.bfs();
        list = m.nodeToPath(node);
        m.mark(list);
        System.out.println(m);
        System.out.println("BFS: More searches (often entire board), but optimal length.\nPath Length: " + m.pathLength + "\nSearch Length: " + m.searchLength );
        m.reset();
        
        node = m.astar();
        list = m.nodeToPath(node);
        m.mark(list);
        System.out.println(m);
        System.out.println("A*: Optimal and less searches.\nPath Length: " + m.pathLength + "\nSearch Length: " + m.searchLength );
        m.reset();
        
        }
    }
    
}




