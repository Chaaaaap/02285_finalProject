public class Pair implements Cloneable {

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

    public Object clone() throws CloneNotSupportedException {
        Pair clone = null;
        try {
            clone = (Pair) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return (((Pair) o).row == this.row && ((Pair) o).col == this.col);
    }
}