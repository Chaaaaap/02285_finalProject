public class Pair{

    public int row;
    public int col;

    @Override
    public String toString(){
        return "row: " + row + " col: " + col;
    }

    public Pair (int row, int col){
        //System.err.println("In Heuristic.Pair 1");

        this.row = row;
        //System.err.println("In Heuristic.Pair 2");

        this.col = col;
        //System.err.println("In Heuristic.Pair 3");

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return (((Pair) o).row == this.row && ((Pair) o).col == this.col);
    }
}