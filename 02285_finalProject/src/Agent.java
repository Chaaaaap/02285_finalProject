
public class Agent implements Cloneable{
    public String color;
    public char name;
    public int row;
    public int col;

    public char[][] goals;

    public Agent(String color, char name) {
        this.color = color;
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Agent clone = null;
        try {
            clone = (Agent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        for (int row = 0 ; row < this.goals.length ; row++){
            for (int col = 0 ; col < this.goals[row].length ; col++){
                if (this.goals[row][col] > 0){
                    s.append("Goal: " + this.goals[row][col] + ", row: " + row + ", col: " +col + "\n");
                }
            }
        }

        return "Agent Color: " + color + ", name: " + name + ", row: " + row + ", col: " + col + "\n" + s.toString();
    }
}