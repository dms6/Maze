package Book;
/**
 * Stores the location of a cell in the grid
 * 
 * @author Dillon 
 * @version 4/21/22
 */
public class MazeLocation{
    public final int row;
    public final int column;
    public MazeLocation(int R, int C){
        row = R;
        column = C;
    }
    public int hashCode(){
        int prime = 31;
        int result = 1;
        result = prime*result+column;
        result = prime*result +row;
        return result;
    }
    
    public boolean equals(Object obj){
        if(this==obj)
            return true;
        if(obj==null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        MazeLocation other = (MazeLocation) obj;
        if(row != other.row)
            return false;
        if(column != other.column)
            return false;
        return true;
    }
    }
