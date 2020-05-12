
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
        return "Color: " + color + ", name: " + name + ", row: " + row + ", col: " + col;
    }
}